package com.haegglundgames.poeng.feature

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View

class GamePanel(context: Context) : SurfaceView(context), SurfaceHolder.Callback, View.OnTouchListener {

    private val thread: MainThread
    private var player1Block: PlayerBlock? = null
    private var player2Block: PlayerBlock? = null
    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    private var ball: Ball? = null
    private var middleLinePaint: Paint? = null

    init {
        holder.addCallback(this)
        setOnTouchListener(this)
        thread = MainThread(holder, this)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        player1Block = PlayerBlock((screenWidth / 2 - 150).toFloat(), 100f, 300f, 100f, Color.RED)
        player2Block = PlayerBlock((screenWidth / 2 - 150).toFloat(), (screenHeight - 100 - 100).toFloat(), 300f, 100f, Color.GREEN)
        ball = Ball((screenWidth / 2).toFloat(), (screenHeight / 2).toFloat(), 50f, 5.0f, 1.0f, 1.0f)
        middleLinePaint = Paint()
        middleLinePaint?.color = Color.GRAY
        middleLinePaint?.strokeWidth = 10.0f

        this.setOnTouchListener(this)
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
        if (event!!.y < screenHeight / 2) {
            player1Block?.update(event.x)
        } else if (event!!.y >= screenHeight / 2) {
            player2Block?.update(event.x)
        }
        return true
    }

    fun update() {
        player1Block?.update()
        player2Block?.update()

        checkScreenCollision()
        checkPlayerCollision()
        ball?.update()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (canvas != null) {
            canvas.drawLine(0.0f, (screenHeight / 2).toFloat(), screenWidth.toFloat(), (screenHeight / 2).toFloat(), middleLinePaint!!)
            player1Block?.draw(canvas)
            player2Block?.draw(canvas)
            ball?.draw(canvas)
        }
    }

    private fun checkScreenCollision() {
        // Check if ball.xPos has collided with left and right screen borders, change direction if so
        if ((ball?.xPos!! - ball?.radius!! <= 0) || (ball?.xPos!! + ball?.radius!! >= screenWidth)) {
            ball?.directionX = ball?.directionX!! * -1.0f
            ball!!.velocity += 0.2f
        }
        // Check if ball.yPos has collided with top and bottom screen borders, change direction if so
        if (ball?.yPos!! - ball?.radius!! <= 0) {
            ball?.directionY = ball?.directionY!! * -1.0f
            ball!!.velocity += 0.2f
            //BottomPlayer scored!
        } else if (ball?.yPos!! + ball?.radius!! >= screenHeight) {
            ball?.directionY = ball?.directionY!! * -1.0f
            ball!!.velocity += 0.2f
            //TopPlayer scored!
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
                }
                ball!!.velocity += 0.2f
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

