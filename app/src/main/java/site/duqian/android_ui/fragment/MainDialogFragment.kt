package site.duqian.android_ui.fragment

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.animation.*
import android.widget.ImageView
import android.widget.RelativeLayout
import site.duqian.android_ui.R
import site.duqian.android_ui.`interface`.OnDialogCallback
import site.duqian.android_ui.utils.UIUtils

/**
 * description:各种各样的dialog
 * @author 杜小菜 Created on 6/13/21 - 11:19 AM.
 * E-mail:duqian2010@gmail.com
 */
class MainDialogFragment : BaseFragment() {

    private lateinit var wrapImageBody: View
    private lateinit var ivTestImage: ImageView
    private lateinit var tvTitle: View

    companion object {
        fun newInstance() = MainDialogFragment()
        private const val ANIM_DURATION = 2000L
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_dialog
    }

    override fun initView(view: View) {
        wrapImageBody = view.findViewById(R.id.iv_test_image)
        ivTestImage = view.findViewById(R.id.iv_test_image)
        tvTitle = view.findViewById(R.id.tv_title)

        tvTitle.setOnClickListener {
            showDialog()
        }
    }

    override fun initData() {
        showDialog()
    }

    private var dialogRect = Rect()
    private fun showDialog() {
        val dialog = CommonDialogFragment.newInstance(true, object : OnDialogCallback {
            override fun onCancel() {

            }

            override fun onDismiss() {
                handleAnimation()
            }

            override fun onBitmapDraw(bitmap: Bitmap?, rect: Rect) {
                //显示并用于做动画
                ivTestImage.setImageBitmap(bitmap)
                ivTestImage.visibility = View.GONE
                dialogRect = rect
            }
        })
        if (fragmentManager != null) {
            dialog.show(fragmentManager!!, "dialog")
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun handleAnimation() {
        ivTestImage.visibility = View.VISIBLE

        //startAnimation()

        //rect1=Rect(686, 324 - 1104, 381) rect=Rect(48, 48 - 768, 1071)
        val rect1 = Rect()
        tvTitle.getGlobalVisibleRect(rect1)
        Log.d("dq-dialog", "rect1=$rect1")
        //relayout(rect1)

        //startAnimation(wrapImageBody)
        //startTranslationAnimation(wrapImageBody)

        doDialogAnimation(wrapImageBody)
    }

    private fun doDialogAnimation(wrapImageBody: View) {
        val valueAnimator = ValueAnimator.ofFloat(1f, 0f)
        val marginStart = UIUtils.dp2px(context, 200f)
        val marginTop = UIUtils.dp2px(context, 400f)
        /*val lp = wrapImageBody.layoutParams as RelativeLayout.LayoutParams
        lp.topMargin = marginTop
        lp.marginStart = marginStart
        wrapImageBody.layoutParams = lp*/

        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            Log.d("dq-dialog", "value=$value")

            //wrapImageBody.alpha = value

            val layoutParams = wrapImageBody.layoutParams as RelativeLayout.LayoutParams
            layoutParams.bottomMargin = (marginTop * value).toInt()
            layoutParams.marginStart = (marginStart * value).toInt()
            wrapImageBody.layoutParams = layoutParams
        }
        valueAnimator.duration = ANIM_DURATION
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.start()
    }

    private fun startAnimation(view: View) {
        val scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 0f)
        val scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 0f)
        val alpha = PropertyValuesHolder.ofFloat("alpha", 1f, 0.2f)
        val animator =
            ObjectAnimator.ofPropertyValuesHolder(view, alpha, scaleX, scaleY)
        animator.duration = ANIM_DURATION
        animator.interpolator = LinearInterpolator()
        animator.start()
    }

    private fun relayout(rect1: Rect) {
        //ivTestImage.scrollTo((rect1.right - rect1.left) / 2, (rect1.bottom - rect1.top) / 2)

        ivTestImage.layout(
            rect1.left,
            rect1.top,
            rect1.right, rect1.bottom
        )
        ivTestImage.requestLayout()
    }

    private fun startTranslationAnimation(view: View) {
        val rect1 = Rect()
        tvTitle.getGlobalVisibleRect(rect1)

        val mAnimatorSet = AnimationSet(true)
        val dx = (dialogRect.right - dialogRect.left) - (rect1.right - rect1.left).toFloat()
        val dy = (dialogRect.bottom - dialogRect.top) - (rect1.bottom - rect1.top).toFloat()
        Log.d("dq-dialog", "dx=$dx,dy=$dy")
        val translateAnimation = TranslateAnimation(0f, -dx / 2, 0f, -dy / 2)

        val scaleAnimation = ScaleAnimation(
            1.0f,
            0.0f,
            1f,
            0.0f,
            ScaleAnimation.ABSOLUTE,
            view.width / 2f,
            ScaleAnimation.ABSOLUTE,
            view.height / 2f
        )
        val alphaAnimation = AlphaAnimation(1.0f, 0.2f)
        //mAnimatorSet.addAnimation(alphaAnimation)
       // mAnimatorSet.addAnimation(scaleAnimation)
        mAnimatorSet.addAnimation(translateAnimation)

        mAnimatorSet.duration = ANIM_DURATION//设置动画变化的持续时间
        //mAnimatorSet.isFillEnabled = true//使其可以填充效果从而不回到原地
        //mAnimatorSet.fillAfter = true//不回到起始位置
        view.startAnimation(mAnimatorSet)
        mAnimatorSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                view.visibility = View.GONE
                view.clearAnimation()
                mAnimatorSet.cancel()
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
    }
}