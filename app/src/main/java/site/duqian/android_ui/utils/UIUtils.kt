package site.duqian.android_ui.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable

/**
 * Description:UI工具类
 * @author 杜小菜,Created on 6/14/21 - 1:53 PM.
 * E-mail:duqian2010@gmail.com
 */
object UIUtils {

    fun drawable2Bitmap(drawable: Drawable?): Bitmap? {
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else if (drawable is NinePatchDrawable) {
            val bitmap = Bitmap
                .createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    if (drawable.getOpacity() != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
                )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight())
            drawable.draw(canvas)
            bitmap
        } else {
            null
        }
    }
}