package dev.maxn.tictaktoe

import androidx.lifecycle.MutableLiveData
import java.lang.Math.pow
import kotlin.math.pow

data class Cell(val num: Int, var state: String = "")

class Game (val human: String, val robot: String, val initDepth: Int){

    private var cur = 0
    var isClosed = false
    private var isMyMove = true
    private var score = 0.0
    var recursiveScore = 0
    var isWinner = false
    var board: MutableLiveData<MutableList<Cell>> = mutableLiveData(mutableListOf())
    var bestMove = -1

    init{
        val tmp: MutableList<Cell> = mutableListOf()
        for (i in 0..8){
            val c = Cell(i)
            tmp.add(c)
        }
        board.value = tmp
    }

fun isFull(): Boolean {
    return board.value!!.count{it.state == ""} <= 0
}

    private fun getScoreForOneLine (values: List<String>): Double {
        var countRobot = 0
        var countHuman = 0
        var adv = 1
        for (v in values){
            if (v == robot)
                countRobot++
            else if (v == human)
                countHuman++
        }

        if (countHuman == 3) {
            isClosed = true
            isWinner = true
        }
        if (countRobot == 3) {
            isWinner = false
            isClosed = true
        }

        if (countHuman == 0) {
            if (isMyMove) {
                adv = 3
            }
            return pow(10.0, countRobot.toDouble()) * adv
        }
        else if (countRobot == 0) {
            if (!isMyMove) {
                adv = 3
            }
            return (-10.0).pow(countHuman.toDouble()) * adv
        }
        return 0.0
    }

    private fun computeScore()
    {
        var ret = 0.0
        val lines = listOf( listOf(0, 1, 2),
            listOf(3, 4, 5),
            listOf(6, 7, 8),
            listOf(0, 3, 6),
            listOf(1, 4, 7),
            listOf(2, 5, 8),
            listOf(0, 4, 8),
            listOf(2, 4, 6)
        )

        for (line in lines){
            ret += getScoreForOneLine(listOf(board.value!![line[0]].state, board.value!![line[1]].state, board.value!![line[2]].state))
        }
        score = ret
    }

    fun move(num: Int, s: String){
        val tmp = board.value
        tmp!![num].state = s
        board.value = tmp
        computeScore()
    }

    private fun unMove(num: Int)
    {
        val tmp = board.value
        tmp!![num].state = ""
        board.value = tmp
        isClosed = false
        isWinner = false
    }

    private fun getMoves(): List<Int>
    {
        val ret = mutableListOf<Int>()
        for (i in 0..8){
            if (board.value!![i].state == ""){
                ret.add(i)
            }
        }
        return ret
    }

    private fun emptyCount(): Int{
        var ans = 0
        for (i in board.value!!){
            if(i.state == "")
                ans++
        }
        return ans
    }

    fun miniMax(depth: Int, needMax: Boolean, a: Int, b: Int): Int{
        var alpha = a
        var beta = b
        isMyMove = needMax
        if (depth == 0 || isClosed || emptyCount() == 0){
            recursiveScore = score.toInt()
            return recursiveScore
        }
        for (i in getMoves()){
            if (depth == initDepth) {
                cur = i
            }
            move(i, if (needMax) robot else human)
            val localScore = miniMax(depth - 1, !needMax, alpha, beta)
            unMove(i)
            if (!needMax){
                if (beta > localScore){
                    beta = localScore
                    if (alpha > beta){
                        break
                    }
                }
            }
            else{
                if (alpha < localScore){
                    alpha = localScore
                    if (depth == initDepth){
                        bestMove = cur
                    }
                    if (alpha > beta){
                        break
                    }
                }
            }
        }
        recursiveScore = if(needMax) alpha else beta
        return recursiveScore
    }
}