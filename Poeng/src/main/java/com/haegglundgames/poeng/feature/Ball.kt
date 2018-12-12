package com.haegglundgames.poeng.feature

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.util.*

class Edge(x: Float, y: Float) {
    var x: Float = x
    var y: Float = y
}
class Ball(startXPos: Float, startYPos: Float, radius: Float, velocity: Float, directionX: Float, directionY: Float) {
    var xPos: Float = startXPos
    var yPos: Float = startYPos
    var radius: Float = radius
    var velocity: Float = velocity
    var directionX: Float = directionX
    var directionY: Float = directionY
    // index 0: left, 1: right, 2: top, 3: bottom
    var edges: Vector<Edge> = Vector(4)

    private val ballPaint: Paint = Paint()

    init {
        for (i in 0..3) {
            this.edges.add(Edge(0.0f, 0.0f))
        }
        this.ballPaint.color = Color.WHITE
    }

    fun update() {
        xPos += directionX * velocity
        yPos += directionY * velocity

        edges[0] = Edge(xPos-radius, yPos) // Left
        edges[1] = Edge(xPos+radius, yPos) // Right
        edges[2] = Edge(xPos, yPos-radius) // Top
        edges[3] = Edge(xPos, yPos+radius) // Bottom
    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(xPos, yPos, radius, ballPaint)
    }

}
