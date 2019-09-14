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

    private lateinit var game: Game
    private var state: MutableLiveData<MutableList<Cell>> = mutableLiveData(mutableListOf())
    private var human: String
    private var robot: String
    private var depth: Int
    private val prefs: SharedPreferences by lazy {
        val ctx = App.applicationContext()
        PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    init {
        human = if (prefs.getBoolean(humanIsX, true)) "x" else "0"
        robot = if (prefs.getBoolean(humanIsX, false)) "0" else "x"
        depth = prefs.getString(LEVEL, "1")!!.toInt()
        val isNew = state.value != null
        game = if (isNew) Game(human, robot, depth)
        else Game(human, robot, depth, state.value!!)
        state.value = game.board.value

        if (getFirstMove() == "human") humanMove() else robotMove()
    }

    fun isFull(): Boolean {
        return game.isFull()
    }

    fun getDrawable(): Int {
        return if (human == "x") R.drawable.anim_x else R.drawable.anim_o
    }

    private fun updatePrefs() {
        human = if (prefs.getBoolean(humanIsX, true)) "x" else "0"
        robot = if (prefs.getBoolean(humanIsX, false)) "0" else "x"
        depth = prefs.getString(LEVEL, "1")!!.toInt()
    }

    fun isPrefsChanged(): Boolean {
        updatePrefs()
        return game.human != human || game.initDepth != depth
    }

    fun initNewGame() {
        updatePrefs()
        game = Game(human, robot, depth)
        state.value = game.board.value
        if (getFirstMove() == "human") humanMove() else robotMove()
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

    fun isWinner(): Boolean {
        return game.isWinner
    }

    fun isClosed(): Boolean {
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