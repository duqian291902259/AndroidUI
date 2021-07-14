package site.duqian.android_ui.view.pageTransformer

import android.annotation.TargetApi
import android.os.Build
import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager.PageTransformer

/**
 * description:viewpager item缩放动画
 *
 * @author 杜小菜 Created on 2021/7/14 - 14:14.
 * E-mail:duqian2010@gmail.com
 */
class ScaleInTransformer : BasePageTransformer {
    private var mMinScale = DEFAULT_MIN_SCALE
    private val mMinAlpha = 0.9f

    constructor()

    @JvmOverloads
    constructor(minScale: Float, pageTransformer: PageTransformer = NonPageTransformer.INSTANCE) {
        mMinScale = minScale
        mPageTransformer = pageTransformer
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public override fun pageTransform(view: View?, position: Float) {
        val pageWidth = view!!.width
        val pageHeight = view.height
        Log.d(
            "dq-banner",
            String.format("pageWidth=%s,pageHeight=%s,position=%s", pageWidth, pageHeight, position)
        );
        view.pivotY = (pageHeight / 2).toFloat()
        view.pivotX = (pageWidth / 2).toFloat()
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.scaleX = mMinScale
            view.scaleY = mMinScale
            view.pivotX = pageWidth.toFloat()
            view.alpha = mMinAlpha
        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            if (position < 0) { //1-2:1[0,-1] ;2-1:1[-1,0]
                val scaleFactor = (1 + position) * (1 - mMinScale) + mMinScale
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                view.pivotX = pageWidth * (DEFAULT_CENTER + DEFAULT_CENTER * -position)
                val alpha = (1 + position) * (1 - mMinAlpha) + mMinAlpha
                view.alpha = alpha
            } else { //1-2:2[1,0] ;2-1:2[0,1]
                val scaleFactor = (1 - position) * (1 - mMinScale) + mMinScale
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                view.pivotX = pageWidth * ((1 - position) * DEFAULT_CENTER)
                val alpha = (1 - position) * (1 - mMinAlpha) + mMinAlpha
                view.alpha = alpha
            }
        } else { // (1,+Infinity]
            view.alpha = mMinAlpha
            view.pivotX = 0f
            view.scaleX = mMinScale
            view.scaleY = mMinScale
        }
    }

    companion object {
        private const val DEFAULT_MIN_SCALE = 0.7f
    }
}