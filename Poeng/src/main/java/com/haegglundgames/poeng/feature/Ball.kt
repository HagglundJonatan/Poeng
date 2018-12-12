package com.haegglundgames.poeng.feature

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Ball(startXPos: Float, startYPos: Float, radius: Float, velocity: Float, directionX: Float, directionY: Float) {
    var xPos: Float = startXPos
    var yPos: Float = startYPos
    var radius: Float = radius
    var velocity: Float = velocity
    var directionX: Float = directionX
    var directionY: Float = directionY
    // index 0: left, 1: right, 2: top, 3: bottom
    var edges: Vector<PointF> = Vector(8)

    private val ballPaint: Paint = Paint()

    init {
        for (i in 0..7) {
            this.edges.add(PointF(0.0f, 0.0f))
        }
        this.ballPaint.color = Color.WHITE
    }

    fun update() {
        xPos += directionX * velocity
        yPos += directionY * velocity

        edges[0] = PointF(xPos-radius, yPos) // Left
        edges[1] = PointF(xPos+radius, yPos) // Right
        edges[2] = PointF(xPos, yPos-radius) // Top
        edges[3] = PointF(xPos, yPos+radius) // Bottom
        edges[4] = PointF(xPos+radius * cos(3 * PI.toFloat() / 4), yPos+radius * sin(3 * PI.toFloat() / 4)) // Top left
        edges[5] = PointF(xPos+radius * cos(PI.toFloat() / 4), yPos+radius * sin(PI.toFloat() / 4)) // Top right
        edges[6] = PointF(xPos+radius * cos(5 * PI.toFloat()/4), yPos+radius * sin(5 * PI.toFloat() / 4)) // Bottom left
        edges[7] = PointF(xPos+radius * cos(7 * PI.toFloat()/4), yPos+radius * sin(7 * PI.toFloat() / 4)) // Bottom right
    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(xPos, yPos, radius, ballPaint)
        for (edge in edges) {
            canvas.drawCircle(edge.x, edge.y, 5.0f, ballPaint)
        }
    }

}
