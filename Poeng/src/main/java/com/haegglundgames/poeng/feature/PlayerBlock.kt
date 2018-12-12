package com.haegglundgames.poeng.feature

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class PlayerBlock(private val posLeft: Float, private val posTop: Float, private val width: Float, private val height: Float, color: Int)
    : RectF(posLeft, posTop, posLeft + width, posTop + height) {
    private val playerPaint: Paint = Paint()

    init {
        this.playerPaint.color = color
    }

    fun update(newCenterPosX: Float = left + width / 2) {
        left = newCenterPosX - width / 2
        right = newCenterPosX + width / 2
    }

    fun draw(canvas: Canvas) {

        canvas.drawRect(this, playerPaint)
    }
}
