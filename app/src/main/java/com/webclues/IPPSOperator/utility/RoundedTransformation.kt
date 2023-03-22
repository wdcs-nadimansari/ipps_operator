package com.webclues.IPPSOperator.utility

import android.graphics.*
import android.graphics.Bitmap.Config


class RoundedTransformation// radius is corner radii in dp
// margin is the board in dp
    (
    private val radius: Int, private val margin: Int  // dp
) : com.squareup.picasso.Transformation {

    override fun transform(source: Bitmap): Bitmap {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val output = Bitmap.createBitmap(source.width, source.height, Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawRoundRect(
            RectF(
                margin.toFloat(),
                margin.toFloat(),
                (source.width - margin).toFloat(),
                (source.height - margin).toFloat()
            ), radius.toFloat(), radius.toFloat(), paint
        )

        if (source != output) {
            source.recycle()
        }

        return output
    }

    override fun key(): String {
        return "rounded"
    }
}