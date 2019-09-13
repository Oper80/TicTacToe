package dev.maxn.tictaktoe

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager

private const val NAME = "name"
private const val LEVEL = "level"
private const val humanIsX = "humanIsX"

class MainViewModel : ViewModel() {

    private var game: Game
    private var state: MutableLiveData<MutableList<Cell>> = mutableLiveData(mutableListOf())
    private val human : String
    private val robot : String
    private val depth : Int
    private val prefs: SharedPreferences by lazy {
        val ctx = App.applicationContext()
        PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    init {
        human = if (prefs.getBoolean(humanIsX, true)) "x" else "0"
        robot = if (prefs.getBoolean(humanIsX, false)) "0" else "x"
        depth = prefs.getString(LEVEL, "1")!!.toInt()
        game = Game(human, robot, depth)
        state.value = game.board.value
    }

    fun isFull(): Boolean {
        return game.isFull()
    }

    fun getDrawable(): Int {
        return if(human == "x") R.drawable.x else R.drawable.o
    }

    fun initNewGame(){
        game = Game(human, robot, depth)
    }

    fun getState(): LiveData<MutableList<Cell>> {
        return state
    }

    fun getFirstMove(): String {
        val humanMove: Boolean = when (human) {
            "x" -> {
                game = Game("x", "0", depth)
                true
            }
            else -> {
                game = Game("0", "x", depth)
                false
            }
        }
        return if (humanMove) "human" else "robot"
    }

    fun isWinner(): Boolean{
        return game.isWinner
    }

    fun isClosed():Boolean{
        return game.isClosed
    }

    fun getBoard(): MutableList<Cell> {
        return game.board.value!!
    }

    fun robotMove() {
        if (!game.isClosed) {
            game.recursiveScore = Int.MIN_VALUE + 1
            val score = game.miniMax(depth, true, Int.MIN_VALUE + 1, Int.MAX_VALUE - 1)
            game.move(game.bestMove, robot)
        }
    }

    fun humanMove() {
        state.value = game.board.value
    }

    fun move(i: Int) {
        game.move(i, human)
        robotMove()
        state.value = game.board.value
    }
}