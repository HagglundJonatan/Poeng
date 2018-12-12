package com.haegglundgames.poeng.feature

import android.graphics.Canvas
import android.view.SurfaceHolder

class MainThread(private val surfaceHolder: SurfaceHolder, private val gamePanel: GamePanel) : Thread() {
    private var isRunning: Boolean = false

    fun setRunning(isRunning: Boolean) {
        this.isRunning = isRunning
    }

    override fun run() {
        while (isRunning) {
            canvas = null
            try {
                canvas = this.surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    this.gamePanel.update()
                    this.gamePanel.draw(canvas)
                }
            } catch (e: Exception) {

            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    companion object {
        var canvas: Canvas? = null
    }
}
