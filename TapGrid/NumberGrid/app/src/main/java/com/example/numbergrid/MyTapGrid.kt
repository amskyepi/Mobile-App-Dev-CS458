package com.example.numbergrid

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import kotlin.math.min

class NumberGrid(context: Context?, attrs: AttributeSet?) : View(context, attrs), GestureDetector.OnGestureListener {

    companion object {
        const val numLines = 8
        const val LINE_WIDTH = 10f
        const val TEXT_SIZE = 130f
    }

    private var numZeros = 9
    private var xCoordinate = 0
    private var yCoordinate = 0
    private var myLength = 0
    private var boxLength = 0
    private var xNumOffset = 0
    private var yNumOffset = 0
    private var mDetector = GestureDetectorCompat(this.context, this)
    private var touchCount =  arrayOf(  arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
                                        arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
                                        arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
                                        arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
                                        arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
                                        arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
                                        arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
                                        arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
                                        arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0))

    private val touchNum = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    private val lineDraw: Paint = Paint()
    private val textDraw: Paint = Paint()
    private val textBounds: Rect = Rect()

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
        boxLength = myLength / numZeros
        lineDraw.textSize = TEXT_SIZE * min((boxLength - numLines * 2).toFloat() / textBounds.width().toFloat(), (boxLength - numLines * 2).toFloat() / textBounds.height().toFloat())
        lineDraw.getTextBounds("0", 0, 1, textBounds)
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

    /* This will Draw our grid of zeros */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        /* Grid Border */
        canvas.drawRect(xCoordinate.toFloat(), yCoordinate.toFloat(), (xCoordinate + myLength).toFloat(), (yCoordinate + myLength).toFloat(), lineDraw)

        /* Grid Lines */
        for (i in 1 .. numLines){
            /* Vertical Lines */
            val verticalLine = (xCoordinate + boxLength * i).toFloat()
            canvas.drawLine(verticalLine, yCoordinate.toFloat(), verticalLine, (yCoordinate + myLength).toFloat(), lineDraw)

            /* Horizontal Lines */
            val horizontalLine = (yCoordinate + boxLength * i).toFloat()
            canvas.drawLine(xCoordinate.toFloat(), horizontalLine, (xCoordinate + myLength).toFloat(), horizontalLine, lineDraw)
        }

        /* Zeros */
        for (row in 0 until numZeros){
            for (column in 0 until numZeros){
                canvas.drawText(touchNum[touchCount[row][column]], (xNumOffset + boxLength * column).toFloat(), (yNumOffset + boxLength * row).toFloat(), textDraw)
            }
        }
    }

    init {
        with(lineDraw) {
            strokeWidth = LINE_WIDTH
            style = Paint.Style.STROKE
        }

        with(textDraw) {
            textAlign = Paint.Align.CENTER
            textSize = TEXT_SIZE
            style = Paint.Style.FILL_AND_STROKE
            getTextBounds(touchNum[0], 0, 1, textBounds)
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
        if(e != null) {
            val column =    if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 1)) 0
                            else if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 2)) 1
                            else if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 3)) 2
                            else if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 4)) 3
                            else if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 5)) 4
                            else if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 6)) 5
                            else if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 7)) 6
                            else if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 8)) 7
                            else if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 9)) 8
                            else if (e.x > xCoordinate && e.x < (xCoordinate + boxLength * 10) && e.x < xCoordinate + myLength) 9
                            else 10

            val row =       if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 1)) 0
                            else if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 2)) 1
                            else if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 3)) 2
                            else if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 4)) 3
                            else if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 5)) 4
                            else if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 6)) 5
                            else if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 7)) 6
                            else if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 8)) 7
                            else if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 9)) 8
                            else if (e.y > yCoordinate && e.y < (yCoordinate + boxLength * 10) && e.y < yCoordinate + myLength) 9
                            else 10
            if (row < 10 && column < 10) {
                touchCount[row][column] = (touchCount[row][column] + 1) % touchNum.size
                invalidate()
            }
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

}