package chess

fun printChessboard(Chessboard: Array<IntArray>) {
    if (Chessboard.isEmpty()) return
    val rowSepLine = "  +"+"---+".repeat(Chessboard[0].count())
    var rowLine: String
    println(rowSepLine)
    for (row in Chessboard.indices.reversed()) {
        rowLine = "${row+1} |"
        for (column in Chessboard[row].indices) {
            rowLine += when (Chessboard[row][column]) {
                1 -> " B |"
                2 -> " W |"
                else -> "   |"
            }
            //println((Chessboard.count() -row).toString()+(column+ 'a'.code).toChar())
        }
        println(rowLine)
        println(rowSepLine)
    }
    rowLine = " "
    for (column in Chessboard[0].indices) {
        rowLine += "   "+(column+ 'a'.code).toChar()
    }
    println(rowLine)
}

fun initChessboard(rowCount: Int, columnCount: Int) :  Array<IntArray>{
    val tmpres: Array<IntArray> = Array(rowCount) {
        IntArray(columnCount) {0} }
    for (row in tmpres.indices) {
        for (column in tmpres[row].indices) {
            tmpres[row][column] = 0
            if (row == rowCount-2) tmpres[row][column] = 1
            if (row == 1) tmpres[row][column] = 2
        }
    }
    return tmpres
}

fun getPlayerColor(idx: Int): String {
    return if (idx == 1) "white" else "black"
}
fun getPlayerColorC(idx: Int): String {
    return if (idx == 1) "White" else "Black"
}

fun checkIsPassant(chessboard: Array<IntArray>, turn: Array<Int>, turnsList: Array<Array<Int>>): Boolean {
    if (turnsList.size < 2) return false
    val eR = turn[2]
    val eC = turn[3]
    val lastTurn = turnsList.last()
    val lbR = lastTurn[0]
    val lbC = lastTurn[1]
    val leR = lastTurn[2]
    val leC = lastTurn[3]
    if (lbC == leC && lbC == eC && eR == (lbR+leR)/2)  {
        chessboard[leR][leC] = 0
        return true
    }
    return false
}

fun checkNextTurnIsStalemate(chessboard: Array<IntArray>, turnsList: Array<Array<Int>>,playerIdx: Int): Boolean {
    val nextPlayerIdx = if (playerIdx == 0) 2 else 1
    val playerDirection = if (nextPlayerIdx == 1) -1 else 1
    for (row in chessboard.indices) {
        for (column in chessboard[row].indices) {
            if (chessboard[row][column]%10 == nextPlayerIdx) {
                for (col in -1..1) {
                    if (column+col < 0 || column+col > chessboard[row].lastIndex) continue
                    if (checkTurnEnd(chessboard,arrayOf(row,column,row+playerDirection,column+col),turnsList,nextPlayerIdx-1,false)) return false
                }
                if ("16".contains(row.toChar()) && checkTurnEnd(chessboard,arrayOf(row,column,row+2*playerDirection,column),turnsList,nextPlayerIdx-1, false)) return false
            }
        }
    }
    return true
}

fun checkNextPlayerPawnsAllCaptured(chessboard: Array<IntArray>, playerIdx: Int): Boolean {
    val nextPlayerIdx = if (playerIdx == 0) 2 else 1
    for (row in chessboard.indices) {
        for (column in chessboard[row].indices) {
            if (chessboard[row][column]%10 == nextPlayerIdx) {
                return false
            }
        }
    }
    return true
}

fun checkTurnEnd(chessboard: Array<IntArray>, turn: Array<Int>, turnsList: Array<Array<Int>>, playerIdx: Int, bprint: Boolean = true): Boolean {
    val bR = turn[0]
    val bC = turn[1]
    val eR = turn[2]
    val eC = turn[3]
    val begTurn = chessboard[bR][bC]
    val endTurn = chessboard[eR][eC]

    if (bprint && begTurn%10 != playerIdx+1) {
        println(String.format("No %s pawn at %s",getPlayerColor(playerIdx),(bC+'a'.code).toChar().toString() + (bR+1).toString()))
        return false
    }

    var moveRowCount = eR -bR
    moveRowCount *= if (playerIdx == 0) -1 else 1
    val moveColumnCount = eC -bC
    val moveDiagonalFront = (kotlin.math.abs(moveColumnCount) == 1) && (moveRowCount == 1)
    val endTurnPlaceTakenOtherPlayer = endTurn%10 == if (playerIdx == 0) 2 else 1
    val tmpres = when (begTurn/10) {
        1 -> false
        else -> {
            moveDiagonalFront && (endTurnPlaceTakenOtherPlayer || checkIsPassant(chessboard,turn,turnsList)) ||
                    (moveColumnCount == 0) && (endTurn == 0) && ((moveRowCount == 1) || (moveRowCount == 2) && ("16".contains(bR.toString())))

        }
    }
    if (bprint && !tmpres) println("Invalid Input")
    return tmpres
}

fun getPlayersNames(): Array<String> {
    val tmpres: Array<String> = Array(2) {""}
    println("First Player's name:")
    tmpres[1] = readln()
    println("Second Player's name:")
    tmpres[0] = readln()
    return tmpres
}

fun main() {
    println(" Pawns-Only Chess")
    val chessboard = initChessboard(8,8)
    val regexTurn = Regex("[a-h][1-8][a-h][1-8]\\b")
    val players = getPlayersNames()
    var turnsList = arrayOf<Array<Int>>()
    printChessboard(chessboard)
    var playerIdx = 1
    do {
        println(players[playerIdx]+"'s turn:")
        val rds = readln()
        if (rds == "exit") break
        if (regexTurn.matches(rds)) {
            val turn = arrayOf(rds[1].digitToInt() -1,rds[0].code -'a'.code,rds[3].digitToInt() -1,rds[2].code -'a'.code)
            if (checkTurnEnd(chessboard,turn,turnsList,playerIdx)) {
                chessboard[turn[2]][turn[3]] = chessboard[turn[0]][turn[1]]
                chessboard[turn[0]][turn[1]] = 0
                printChessboard(chessboard)
                turnsList += turn
                if ("18".contains(rds[3]) || checkNextPlayerPawnsAllCaptured(chessboard,playerIdx)) {
                    println(getPlayerColorC(playerIdx)+" Wins!")
                    break
                }
                if (checkNextTurnIsStalemate(chessboard,turnsList,playerIdx)) {
                    println("Stalemate!")
                    break
                }
                playerIdx = if (playerIdx == 0) 1 else 0
            }
        } else {
            println("Invalid Input")
        }
    } while (true)
    println("Bye!")
}