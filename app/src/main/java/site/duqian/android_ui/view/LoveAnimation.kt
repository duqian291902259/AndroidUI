package site.duqian.android_ui.view

import android.animation.*
import android.content.Context
import android.graphics.PointF
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import site.duqian.android_ui.R
import site.duqian.android_ui.utils.UIUtils.dp2px
import java.util.*
import kotlin.math.abs

/**
 * Description:比心、点赞类的动画管理
 *
 * @author 杜小菜, Created on 7/11/21 - 10:08 PM.
 * E-mail:duqian2010@gmail.com
 */
class LoveAnimation(
    private val mRootView: RelativeLayout,
    private val mViewFrom: View,
    private val mViewTo: View
) {
    private val mContext: Context = mRootView.context
    private val mRandom = Random()

    //图片的原始高度
    private val mViewWidth: Int
    private val mViewHeight: Int

    //移动范围的宽高
    private val mWidth: Int
    private val mHeight: Int
    private val mRectFrom: Rect = Rect()
    private val mRectTo: Rect = Rect()

    fun startAnimation() {
        val iv = ImageView(mViewFrom.context)
        iv.setImageResource(R.mipmap.ic_avatar_duqian)
        //将iv添加到父容器底部、水平居中位置
        val params = RelativeLayout.LayoutParams(mViewWidth, mViewWidth)
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        params.addRule(RelativeLayout.ALIGN_PARENT_END)
        params.marginEnd = dp2px(mContext, 10f)
        params.bottomMargin = dp2px(mContext, 80f)
        iv.layoutParams = params
        mRootView.addView(iv)

        //开始属性动画：平移、透明度渐变、缩放动画
        val animatorSet = getAnimator(iv)

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                mRootView.removeView(iv)
                Log.d("dq-anim", "end anim")
            }
        })
        animatorSet.duration = ANIM_DURATION.toLong()
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.start()
    }

    // 得到一个iv的动画集合
    private fun getAnimator(animView: View): AnimatorSet {
        //平移、透明度渐变、缩放动画
        val alpha = ObjectAnimator.ofFloat(animView, "alpha", 0.3f, 1f)
        val scaleX = ObjectAnimator.ofFloat(animView, "scaleX", 0.3f, 1f)
        val scaleY = ObjectAnimator.ofFloat(animView, "scaleY", 0.3f, 1f)
        val enter = AnimatorSet()
        enter.duration = ANIM_DURATION.toLong()
        enter.playTogether(alpha, scaleX, scaleY)

        //设置平移的曲线动画---贝塞尔曲线，启动估值器
        val bezierAnimator = getBezierValueAnimator(animView)
        val set = AnimatorSet()
        set.playSequentially(enter, bezierAnimator)
        set.setTarget(animView)
        return set
    }

    //得到一个贝塞尔曲线动画
    private fun getBezierValueAnimator(animView: View): ValueAnimator {
        //根据贝塞尔公式确定四个点（起始点p0，拐点1p1，拐点2p2，终点p3）
        //PointF pointF0 = new PointF((mWidth - mViewWidth) / 2f, mHeight - mViewHeight);
        //PointF pointF3 = new PointF(mRandom.nextInt(mWidth), 0);
        val pointF0 = PointF(mRectFrom.left.toFloat(), mRectFrom.top.toFloat())
        val pointF3 = PointF(mRectTo.left.toFloat(), mRectTo.top.toFloat())
        val pointF1 = getPointF(1)
        val pointF2 = getPointF(2)
        val evaluator = BezierEvaluator(pointF1, pointF2)
        val animator = ValueAnimator.ofObject(evaluator, pointF0, pointF3)
        animator.addUpdateListener { animation: ValueAnimator ->
            val pointF = animation.animatedValue as PointF
            animView.x = pointF.x
            animView.y = pointF.y
            Log.d("dq-anim", "坐标是:x" + pointF.x + "  y:" + pointF.y)
            animView.alpha = 1 - animation.animatedFraction //1~0 百分比
        }
        animator.duration = ANIM_DURATION.toLong()
        return animator
    }

    private fun getPointF(i: Int): PointF {
        val pointF = PointF()
        pointF.x = mRandom.nextInt(mWidth).toFloat()
        if (i == 1) {
            pointF.y = mRandom.nextInt(mHeight / 2) + mHeight / 2f
        } else {
            pointF.y = mRandom.nextInt(mHeight / 2).toFloat()
        }
        return pointF
    }

    internal class BezierEvaluator(private val pointF1: PointF, private val pointF2: PointF) :
        TypeEvaluator<PointF> {
        override fun evaluate(t: Float, point0: PointF, point3: PointF): PointF {
            // b(t)=p0*(1-t)*(1-t)*(1-t)+3*p1*t*(1-t)*(1-t)+3*p2*t*t*(1-t)+p3*t*t*t
            val point = PointF()
            point.x =
                point0.x * (1 - t) * (1 - t) * (1 - t) + 3 * pointF1.x * t * (1 - t) * (1 - t) + 3 * pointF2.x * t * t * (1 - t) + point3.x * t * t * t
            point.y =
                point0.y * (1 - t) * (1 - t) * (1 - t) + 3 * pointF1.y * t * (1 - t) * (1 - t) + 3 * pointF2.y * t * t * (1 - t) + point3.y * t * t * t
            return point
        }
    }

    companion object {
        private const val ANIM_DURATION = 2 * 1000
    }

    init {
        mViewFrom.getGlobalVisibleRect(mRectFrom)
        mViewTo.getGlobalVisibleRect(mRectTo)
        mViewWidth = mViewFrom.measuredWidth
        mViewHeight = mViewFrom.measuredHeight
        mWidth = abs(mRectFrom.right - mRectTo.left)
        mHeight = abs(mRectFrom.bottom - mRectTo.top)
    }
}