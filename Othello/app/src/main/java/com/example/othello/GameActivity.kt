/* Othello
 * Written by Amethyst Skye
 */

package com.example.othello

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat

@SuppressLint("ResourceAsColor")
class GameActivity @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), GestureDetector.OnGestureListener {

    /* Grid line information */
    companion object {
        const val numLines = 7
        const val LINE_WIDTH = 10f
    }

    /* 8 x 8 dimensions */
    private var numBoxes = 8

    /* Variables used to draw items on the screen, within grid we draw */
    private var xCoordinate = 0
    private var yCoordinate = 0
    private var myLength = 0
    private var boxLength = 0
    private var xNumOffset = 0
    private var yNumOffset = 0

    /* For tap listening */
    private var mDetector = GestureDetectorCompat(this.context, this)

    /* Array containing the current gameboard and player stone locations */
    /* Black = Player 1 ; White = Player 2 */
    private var gameBoard = Array(numBoxes){(Array(numBoxes){0})}

    /* Array containing current legal moves for current player */
    /* Initialized all spaces to 0 and handled later */
    private var legalMoves = Array(numBoxes){(Array(numBoxes){0})}

    /* Black goes first */
    private var playerTurn: Int = 1

    /* Game state flag */
    var gameState = false


    /* Paint methods used in our game */
    private val linePaint = Paint()
    private val p1CirclePaint = Paint()
    private val p2CirclePaint = Paint()
    private val p1CirclePaintTrans = Paint()
    private val p2CirclePaintTrans = Paint()

    /* Draws a border around our grid */
    private val textBounds: Rect = Rect()

    private var observer: GridObserver? = null

    /* Resizes board to fit with any screen dimension */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > h){
            myLength = height - numLines * 2
            xCoordinate = width / 2 - myLength / 2
            yCoordinate = numLines
        }
        else{
            myLength = width - numLines * 2
            xCoordinate = numLines
            yCoordinate = height / 2 - myLength / 2
        }
        boxLength = myLength / numBoxes
        linePaint.getTextBounds("0", 0, 1, textBounds)
        xNumOffset = xCoordinate + boxLength / 2
        yNumOffset = yCoordinate + textBounds.height() / 2 +  boxLength / 2
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(mDetector.onTouchEvent(event)) {
            return true
        }
        return super.onTouchEvent(event)
    }

    /* Draws the grid, and player stones */
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        /* Grid Border */
        canvas.drawColor(Color.rgb(50, 90, 20))
        canvas.drawRect(xCoordinate.toFloat(), yCoordinate.toFloat(), (xCoordinate + myLength).toFloat(), (yCoordinate + myLength).toFloat(), linePaint)

        val stoneRad = (myLength / (numLines * 3)).toFloat()

        /* Stone Layout [row][col] aka [y][x]*/
        for (row in 0 .. numLines){
            for (col in 0 .. numLines){
                if (gameBoard[row][col] == 1)
                    canvas.drawCircle((xCoordinate + boxLength * (col + 0.5)).toFloat(), (yNumOffset + (boxLength * row)).toFloat(), stoneRad, p1CirclePaint)
                if (gameBoard[row][col] == 2)
                    canvas.drawCircle((xCoordinate + boxLength * (col + 0.5)).toFloat(), (yNumOffset + (boxLength * row)).toFloat(), stoneRad, p2CirclePaint)
            }
        }

        /* Current player's possible moves */
        getLegalMoves()
        for (row in 0 .. numLines){
            for (col in 0 .. numLines){
                if (legalMoves[row][col] == 1)
                    canvas.drawCircle((xCoordinate + boxLength * (col + 0.5)).toFloat(), (yNumOffset + (boxLength * row)).toFloat(), stoneRad, p1CirclePaintTrans)
                if (legalMoves[row][col] == 2)
                    canvas.drawCircle((xCoordinate + boxLength * (col + 0.5)).toFloat(), (yNumOffset + (boxLength * row)).toFloat(), stoneRad, p2CirclePaintTrans)
            }
        }

        /* Grid Lines */
        for (i in 1 .. numLines){
            /* Vertical Lines */
            val verticalLine = (xCoordinate + boxLength * i).toFloat()
            canvas.drawLine(verticalLine, yCoordinate.toFloat(), verticalLine, (yCoordinate + myLength).toFloat(), linePaint)

            /* Horizontal Lines */
            val horizontalLine = (yCoordinate + boxLength * i).toFloat()
            canvas.drawLine(xCoordinate.toFloat(), horizontalLine, (xCoordinate + myLength).toFloat(), horizontalLine, linePaint)
        }

    }

    /* Handles all paint methods */
    init {
        with(linePaint) {
            strokeWidth = LINE_WIDTH
            color = Color.BLACK
            style = Paint.Style.STROKE
        }

        with(p1CirclePaint) {
            style = Paint.Style.FILL_AND_STROKE
            color = Color.BLACK
        }

        with(p1CirclePaintTrans) {
            style = Paint.Style.FILL_AND_STROKE
            color = R.color.transBlack
        }

        with(p2CirclePaint) {
            style = Paint.Style.FILL_AND_STROKE
            color = Color.WHITE
        }

        with(p2CirclePaintTrans) {
            style = Paint.Style.FILL_AND_STROKE
            color = R.color.transWhite
        }
    }

    /* Resets the board with player stones in their starting positions */
    fun resetBoard(){
        /* Clear board */
        for (x in 0 .. numLines){
            for (y in 0 .. numLines){
                gameBoard[x][y] = 0
            }
        }
        /* Set black and white stones to starting position */
        gameBoard[3][3] = 2
        gameBoard[3][4] = 1
        gameBoard[4][3] = 1
        gameBoard[4][4] = 2
        playerTurn = 1
        gameState = true
    }

    /* Returns true if in bounds, false if not */
    private fun inBounds(row: Int, col: Int): Boolean{
        return (row in 0..7) && (col in 0..7)
    }

    /* Sets Player Score */
    fun setPlayerScore(player: Int) :Int{
        var score = 0
        for (row in 0 .. numLines){
            for(column in 0 .. numLines){
                if(gameBoard[row][column] == player)
                    score += 1
            }
        }
        return score
    }

    /* Gets Player score */
    fun getPlayerScore(player: Int) :Int{
        var score = 0
        for (row in 0 .. numLines){
            for(column in 0 .. numLines){
                if(player == 1){
                    if(gameBoard[row][column] == 1)
                        score += 1
                }
                if(player == 2){
                    if(gameBoard[row][column] == 2)
                        score += 1
                }
            }
        }
        return score
    }

    /* Checks status of game to print to game message box */
    fun checkPlayer(): Int{
        return playerTurn
    }

    /* Returns true if valid move, and false if not */
    private fun getLegalMoves(){
        /* Clear any previous tokens for legal moves */
        legalMoves = Array(numBoxes){(Array(numBoxes){0})}

        /* Check all adjacent spaces */
        for (row in 0 .. numLines){
            for (col in 0 .. numLines){
                if (gameBoard[row][col] == 0){
                    val nn = adjacentSupport(-1, 0, row, col)
                    val nw = adjacentSupport(-1, -1, row, col)
                    val ne = adjacentSupport(-1, 1, row, col)

                    val ww = adjacentSupport(0, -1, row, col)
                    val ee = adjacentSupport(0, 1, row, col)

                    val ss = adjacentSupport(1, 0, row, col)
                    val sw = adjacentSupport(1, -1, row, col)
                    val se = adjacentSupport(1, 1, row, col)

                    if(nn || nw || ne || ww || ee || ss || sw || se){
                        legalMoves[row][col] = playerTurn
                    }
                }
            }
        }
    }

    /* Checks where legal moves are for the current player */
    private fun adjacentSupport(dRow: Int, dCol: Int, row: Int, col: Int): Boolean {
        /* other player */
        val other = when (playerTurn) {
            1 -> 2
            2 -> 1
            else -> {
                print("Invalid player selection")
                return false
            }
        }
        /* Check that adjacent support is on board */
        if(!inBounds(row + dRow, col + dCol)) return false

        /* Check if other player is in space */
        if (gameBoard[row + dRow][col + dCol] != other) return false

        /* Check if other player is in space */
        if (gameBoard[row + dRow][col + dCol] == playerTurn) return false

        /* Check if there is space for token */
        if(!inBounds(row + (dRow * 2), col + (dCol * 2))) return false

        return(checkLineMatch(dRow, dCol, row, col))
    }

    /* Recursive call to check for legal moves */
    private fun checkLineMatch(dRow: Int, dCol: Int, row: Int, col: Int): Boolean {
        if(gameBoard[row][col] == playerTurn) return true
        /* Check that adjacent support is on board */
        if(!inBounds(row+dRow, col+dCol)) return false
        return(checkLineMatch(dRow, dCol, row + dRow, col + dCol))
    }

    /* Determines which stones need to be flipped after player makes move */
    private fun checkFlip(row: Int, column: Int){
        /* Check nn, nw, ne */
        flipPath(-1, 0, row, column)
        flipPath(-1, -1, row, column)
        flipPath(-1, 1, row, column)

        /* Check ww, ee */
        flipPath(0, -1, row, column)
        flipPath(0, 1, row, column)

        /* Check ss, sw, se */
        flipPath(1, 0, row, column)
        flipPath(1, -1, row, column)
        flipPath(1, 1, row, column)
    }

    /* Recursive call which checks path for stones to be flipped */
    private fun flipPath(dRow: Int, dCol: Int, row: Int, col: Int): Boolean{

        /* Check if within board grid */
        if(!inBounds(row + dRow, col + dCol)) return false

        /* Don't flip if encountering an empty space */
        if (gameBoard[row + dRow][col + dCol] == 0) return false

        /* Don't flip if we encounter ourself, and this is end of flipping */
        if (gameBoard[row + dRow][col + dCol] == playerTurn) return true

        return when {
            flipPath(dRow, dCol, row + dRow, col + dCol) -> {
                gameBoard[row + dRow][col + dCol] = playerTurn
                true
            }
            else -> false
        }
    }

    /* Motion Event Functions */
    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent?) {

    }

    /* So many cursed else if cases, but it works */
    /* This will check the coordinates of the tap, and change the corresponding cell */
    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        if((e != null)) {
            /* Check if legalMoves array is null */
            /* If so, game over */
            if (legalMoves.contentEquals((Array(numBoxes){(Array(numBoxes){0})}))){
                gameState = false
                invalidate()
                notifyObserver()
            }
            val column =    if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 1)) 0
                            else if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 2)) 1
                            else if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 3)) 2
                            else if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 4)) 3
                            else if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 5)) 4
                            else if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 6)) 5
                            else if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 7)) 6
                            else if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 8)) 7
                            else if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 9) && (e.x < xCoordinate + myLength)) 8
                            else 9

            val row =   if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 1)) 0
                        else if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 2)) 1
                        else if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 3)) 2
                        else if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 4)) 3
                        else if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 5)) 4
                        else if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 6)) 5
                        else if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 7)) 6
                        else if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 8)) 7
                        else if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 9 ) && (e.y < yCoordinate + myLength)) 8
                        else 9
            if (row < numBoxes && column < numBoxes) {
                /* If a legal move */
                if (legalMoves[row][column] == playerTurn) {
                    /* Change stones as needed */
                    gameBoard[row][column] = playerTurn
                    checkFlip(row, column)

                    /* Switch Player Turn */
                    playerTurn = when (playerTurn) {
                        1 -> 2
                        else -> {1}
                    }
                    /* Refresh screen */
                    invalidate()
                    notifyObserver()
                }
            }
            else return false
        }
        return true
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {

    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }

    fun registerObserver(obs: GridObserver) {
        observer = obs
    }

    private fun notifyObserver() {
        if(observer != null) {
            observer?.gridUpdated()
        }
    }

}