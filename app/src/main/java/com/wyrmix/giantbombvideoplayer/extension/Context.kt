package com.wyrmix.giantbombvideoplayer.extension

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat

/**
 * Context extension methods
 *
 * Created by kriedema on 6/27/17.
 */
fun Context.getActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun Context.generateBitmapFromRes(resId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(this, resId)
    drawable?.apply {
        setBounds(
                0,
                0,
                intrinsicWidth,
                intrinsicHeight)
        val bitmap = Bitmap.createBitmap(
                intrinsicWidth,
                intrinsicHeight,
                Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)
        return bitmap
    }
    return null
}
