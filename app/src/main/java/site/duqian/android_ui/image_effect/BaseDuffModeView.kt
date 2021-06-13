package site.duqian.android_ui.image_effect

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
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
abstract class BaseDuffModeView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var paint: Paint? = null
    private var mWidth = 0
    private var mHeight = 0
    private var srcBm: Bitmap? = null
    private var dstBm: Bitmap? = null
    var mMode: Int = 0
    var mPorterDuffMode: PorterDuff.Mode = PorterDuff.Mode.SRC_IN

    init {
        val obtainStyledAttributes =
            context?.obtainStyledAttributes(attrs, R.styleable.BaseDuffModeView)
        mMode = obtainStyledAttributes?.getInt(R.styleable.BaseDuffModeView_mode, 0) ?: 0
        obtainStyledAttributes?.recycle()

        //禁用硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        //初始化画笔
        paint = Paint()

        updatePorterDuffMode()
    }

    fun updatePorterDuffMode() {
        for (mode in PorterDuff.Mode.values()) {
            Log.d("dq-android-ui", "name=${mode.name},ordinal=${mode.ordinal}")
            if (mMode == mode.ordinal) {
                mPorterDuffMode = mode
                break
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mWidth = right - left
        mHeight = bottom - top
        srcBm = createSrcBitmap(mWidth, mHeight)
        dstBm = createDstBitmap(mWidth, mHeight)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(dstBm!!, 0f, 0f, paint)
        if (mMode > 0) {
            paint?.xfermode = PorterDuffXfermode(mPorterDuffMode)
        }
        canvas.drawBitmap(srcBm!!, 0f, 0f, paint)
        paint?.xfermode = null

        paint?.style = Paint.Style.FILL
        paint?.strokeWidth = 12f
        paint?.textSize = 30f
        paint?.color = Color.parseColor("#cccccc")
        canvas.drawText(mPorterDuffMode.name, 0f, mHeight/2.0f, paint!!)
    }

    abstract fun createDstBitmap(width: Int, height: Int): Bitmap

    abstract fun createSrcBitmap(width: Int, height: Int): Bitmap
}