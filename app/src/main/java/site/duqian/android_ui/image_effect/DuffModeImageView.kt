package site.duqian.android_ui.image_effect

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import site.duqian.android_ui.R

/**
 * Description:两图融合
 *
 * 源图像和目标图像的交集区域，只会显示出目标图像的效果，但是其透明度会受到源图像的透明度的影响。
 *
 * DST_IN 模式，它是根据 [Sa * Da, Sa * Dc] 算法来进行绘制
 * 交集区域的透明度 = 源图像的透明度 * 目标图像的透明度
 * 交集区域的色值 = 源图像的透明度 * 目标图像的色值
 *
 * @author 杜小菜, Created on 6/13/21 - 5:45 PM.
 * E-mail:duqian2010@gmail.com
 */
class DuffModeImageView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseDuffModeView(context, attrs, defStyleAttr) {

    private var mBorderBitmap: Bitmap? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mBorderBitmap == null) {
            mBorderBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_rect_blue)
        }
        canvas.drawBitmap(mBorderBitmap!!, null, mRectBorder!!, paint)
    }

    override fun createDstBitmap(width: Int, height: Int): Bitmap {
        return getBorderBitmap(width, height)
        //return getAvatarBitmap(width, height)
    }

    /**
     * Rect src: 是对图片进行裁截，若是空null则显示整个图片
     * RectF dst：是图片在Canvas画布中显示的区域，
     * 大于src则把src的裁截区放大，
     * 小于src则把src的裁截区缩小。
     * 第一个Rect 代表要绘制的bitmap 区域，第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方
     */
    override fun createSrcBitmap(width: Int, height: Int): Bitmap {
        return getAvatarBitmap(width, height)
        //return getBorderBitmap(width, height)
    }

    /**
     * 原图不能修改，需要copy
     * Caused by: java.lang.IllegalStateException: Immutable bitmap passed to Canvas constructor
     */
    private fun getAvatarBitmap(width: Int, height: Int): Bitmap {
        val bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_avator_duqian)
            .copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmap)
        val dstPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        //val src = Rect(0, 0, bitmap.width, bitmap.height)
        val rect = Rect(0, 0, width, height)
        canvas.drawBitmap(
            bitmap, null,
            rect,
            dstPaint
        )
        return bitmap
    }

    private fun getBorderBitmap(width: Int, height: Int): Bitmap {
        val bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_rect_blue_shape)
        /*    .copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmap)
        val dstPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = Rect(0, 0, width, height)
        canvas.drawBitmap(
            bitmap, null,
            rect,
            dstPaint
        )*/
        return bitmap
    }

    fun setMode(mode: Int) {
        this.mMode = mode
        updatePorterDuffMode()
        invalidate()
    }

    fun setPorterDuffMode(porterDuffMode: PorterDuff.Mode) {
        this.mPorterDuffMode = porterDuffMode
        invalidate()
    }
}