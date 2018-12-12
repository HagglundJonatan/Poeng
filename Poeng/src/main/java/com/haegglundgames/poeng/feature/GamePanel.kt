package com.haegglundgames.poeng.feature

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.SparseArray
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import java.util.*

enum class GameOver {
    GO_NONE,
    GO_PLAYER_1_WON,
    GO_PLAYER_2_WON
}

class GamePanel(context: Context) : SurfaceView(context), SurfaceHolder.Callback, View.OnTouchListener {

    private val thread: MainThread
    private var player1Block: PlayerBlock? = null
    private var player2Block: PlayerBlock? = null
    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    private var ball: Ball? = null
    private var middleLinePaint: Paint? = null
    private var textPaint: Paint? = null
    private var gameOver: GameOver = GameOver.GO_NONE
    private var activePointers: SparseArray<PointF>? = null

    init {
        holder.addCallback(this)
        setOnTouchListener(this)
        thread = MainThread(holder, this)
        isFocusable = true
        middleLinePaint = Paint()
        middleLinePaint?.color = Color.GRAY
        middleLinePaint?.strokeWidth = 10.0f
        textPaint = Paint()
        textPaint?.color = Color.YELLOW
        textPaint?.textSize = 100.0f
        activePointers = SparseArray()
    }

    private fun setupGameComponents() {
        player1Block = PlayerBlock((screenWidth / 2 - 150).toFloat(), 100f, 300f, 100f, Color.RED)
        player2Block = PlayerBlock((screenWidth / 2 - 150).toFloat(), (screenHeight - 100 - 100).toFloat(), 300f, 100f, Color.GREEN)

        var directionX: Float = (-1..1).shuffled().last().toFloat()
        if (directionX == 0.0f) {
            directionX = 1.0f
        }
        var directionY: Float = (-1..1).shuffled().last().toFloat()
        if (directionY == 0.0f) {
            directionY = -1.0f
        }

        ball = Ball((screenWidth / 2).toFloat(), (screenHeight / 2).toFloat(), 50f, 10.0f, directionX, directionY)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        setupGameComponents()
        setOnTouchListener(this)
        thread.setRunning(true)
        thread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        player1Block?.update()
        player2Block?.update()
        ball?.update()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        while (retry) {
            try {
                thread.setRunning(false)
                thread.join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            retry = false
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        var pointerIndex = event!!.actionIndex

        var pointerId = event?.getPointerId(pointerIndex)

        var maskedAction = event?.actionMasked

        when (maskedAction) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> {
                var pointF = PointF()
                pointF.x = event.getX(pointerIndex)
                pointF.y = event.getY(pointerIndex)
                activePointers?.put(pointerId, pointF)

                if ((pointF.y > screenHeight / 8 * 3) && (pointF.y < screenHeight / 8 * 5) &&
                        (gameOver != GameOver.GO_NONE)) {
                    gameOver = GameOver.GO_NONE
                    setupGameComponents()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                var i = 0
                while (i < event?.pointerCount) {
                    var pointF = activePointers?.get(event.getPointerId(i))
                    pointF?.let {
                        pointF.x = event.getX(i)
                        pointF.y = event.getY(i)

                        if (pointF.y < screenHeight / 8 * 3) {
                            player1Block?.update(pointF.x)
                        } else if (pointF.y >= screenHeight / 8 * 5) {
                            player2Block?.update(pointF.x)
                        }
                        else {}
                    }
                    ++i
                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_CANCEL -> {
                activePointers?.remove(pointerId)
            }
        }
        return true
    }

    fun update() {
        if (gameOver == GameOver.GO_NONE) {
            checkScreenCollision()
            checkPlayerCollision()
            ball?.update()
            player1Block?.update()
            player2Block?.update()
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (canvas != null) {
            canvas.drawLine(0.0f, (screenHeight / 2).toFloat(), screenWidth.toFloat(), (screenHeight / 2).toFloat(), middleLinePaint!!)
            player1Block?.draw(canvas)
            player2Block?.draw(canvas)
            ball?.draw(canvas)
            if (gameOver != GameOver.GO_NONE) {
                val winnerString = "PLAYER ${gameOver.ordinal} WON!"
                canvas.drawText(winnerString, screenWidth / 2.0f - 350, screenHeight / 2.0f, textPaint)
            }
        }
    }

    private fun checkScreenCollision() {
        // Check if ball.xPos has collided with left and right screen borders, change direction if so
        if ((ball?.xPos!! - ball?.radius!! <= 0) || (ball?.xPos!! + ball?.radius!! >= screenWidth)) {
            ball?.directionX = ball?.directionX!! * -1.0f
            ball!!.velocity += 0.02f
        }
        // Check if ball.yPos has collided with top and bottom screen borders, change direction if so
        if (ball?.yPos!! - ball?.radius!! <= 0) {
            ball?.directionY = ball?.directionY!! * -1.0f
            ball!!.velocity += 0.02f
            //BottomPlayer scored!
            gameOver = GameOver.GO_PLAYER_2_WON
        } else if (ball?.yPos!! + ball?.radius!! >= screenHeight) {
            ball?.directionY = ball?.directionY!! * -1.0f
            ball!!.velocity += 0.02f
            //TopPlayer scored!
            gameOver = GameOver.GO_PLAYER_1_WON
        }
    }

    private fun checkPlayerCollision() {
        // Check 4 edge x,y coordinates if they are within the player blocks.
        for (edgeIndex in ball!!.edges.indices) {
            if (player1Block!!.contains(ball!!.edges[edgeIndex].x, ball!!.edges[edgeIndex].y)) {
                // Collision! Change direction.
                when (edgeIndex) {
                    0, 1 -> ball?.directionX = ball?.directionX!! * -1.0f
                    2, 3 -> ball?.directionY = ball?.directionY!! * -1.0f
                    4, 5, 6, 7 -> {
                        ball?.directionX = ball?.directionX!! * -1.0f
                        ball?.directionY = ball?.directionY!! * -1.0f
                    }
                }
                ball!!.velocity += 0.02f
            }

            if (player2Block!!.contains(ball!!.edges[edgeIndex].x, ball!!.edges[edgeIndex].y)) {
                // Collision! Change direction.
                when (edgeIndex) {
                    0, 1 -> ball?.directionX = ball?.directionX!! * -1.0f
                    2, 3 -> ball?.directionY = ball?.directionY!! * -1.0f
                }
                ball!!.velocity += 0.02f
            }
        }
    }
}

