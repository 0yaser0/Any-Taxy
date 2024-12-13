package com.cmc.mytaxi.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.sin

class WaveAnimation(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paints = listOf(
        Paint().apply {
            color = Color.parseColor("#2BCE72")
            style = Paint.Style.FILL
        },
        Paint().apply {
            color = Color.parseColor("#108C73")
            style = Paint.Style.FILL
        },
        Paint().apply {
            color = Color.parseColor("#CC3D4E")
            style = Paint.Style.FILL
        },
        Paint().apply {
            color = Color.parseColor("#E6BB11")
            style = Paint.Style.FILL
        }
    )

    private var phase = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()
        val amplitude = 20f
        val frequency = 0.5f

        paints.forEachIndexed { index, paint ->
            val path = Path()
            path.moveTo(0f, 0f)
            for (x in 0..width.toInt()) {
                val y = amplitude * sin((x + phase + index * 45) * frequency * Math.PI / 180).toFloat() + height / 4 * index
                path.lineTo(x.toFloat(), y)
            }

            path.lineTo(width, height)
            path.lineTo(0f, height)
            path.close()

            canvas.drawPath(path, paint)
        }

        phase += 2
        invalidate()
    }
}
