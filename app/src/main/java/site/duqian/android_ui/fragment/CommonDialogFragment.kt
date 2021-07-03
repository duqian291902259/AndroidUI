package site.duqian.android_ui.fragment

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.*
import site.duqian.android_ui.R
import site.duqian.android_ui.utils.UIUtils


/**
 * description:自定义dialog
 * @author 杜小菜 Created on 6/30/21 - 10:02 PM.
 * E-mail:duqian2010@gmail.com
 */
class CommonDialogFragment : BaseDialogFragment() {

    private lateinit var wrapDialogContent: View
    private lateinit var tvTitle: View
    private var mWidth = 0
    private var mHeight = 0

    companion object {
        @JvmOverloads
        fun newInstance(
            cancelable: Boolean = true,
            cancelListener: OnDialogCancelListener? = null
        ): BaseDialogFragment {
            val instance = CommonDialogFragment()
            instance.isCancelable = cancelable
            instance.mCancelListener = cancelListener
            return instance
        }
    }

    override fun getDialog(context: Context?): Dialog? {
        return null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_common_dialog, null)
        wrapDialogContent = rootView.findViewById(R.id.wrap_dialog_content)
        tvTitle = rootView.findViewById(R.id.tv_title)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            mWidth = view.measuredWidth
            mHeight = view.measuredHeight
        }
        wrapDialogContent.setOnClickListener {

            val mAnimatorSet = AnimationSet(true)
            val animation = TranslateAnimation(0f, 300f, 500f, 500f)
            animation.duration = 1200//设置动画变化的持续时间
            animation.isFillEnabled = true//使其可以填充效果从而不回到原地
            animation.fillAfter = true//不回到起始位置
            mAnimatorSet.addAnimation(animation)
            tvTitle.startAnimation(mAnimatorSet)


            mAnimatorSet.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    //doDialogAnimation()
                    doScaleAnimation()

                    updateDialogLayout(0.5f)
                }

                override fun onAnimationRepeat(animation: Animation?) {

                }
            })
        }
    }

    private fun doDialogAnimation() {
        val valueAnimator = ValueAnimator.ofFloat(1f, 0.2f)
        valueAnimator.addUpdateListener {
            //val value = it.animatedFraction
            val value = it.animatedValue as Float
            updateDialogLayout(value)
        }
        valueAnimator.duration = 5000
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.start()
    }

    private fun updateDialogLayout(value: Float) {
        val window: Window? = dialog?.window
        val windowParams: WindowManager.LayoutParams? = window?.attributes
        windowParams?.width = 1000 * value.toInt()
        windowParams?.height = 1000 * value.toInt()
        //window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        //window?.setGravity(Gravity.START or Gravity.TOP)
        //显示的坐标
        windowParams?.x = 1500 * value.toInt()
        windowParams?.y = 500 * value.toInt()
        //windowParams?.horizontalMargin = UIUtils.getScreenWidth(context) * value
        //windowParams?.verticalMargin = UIUtils.getScreenHeight(context) * value
        window?.attributes = windowParams
        Log.d("dq-dialog", "window=$window,value=$value.windowParams")
    }

    private fun doScaleAnimation() {
        val scaleAnimation = ScaleAnimation(1.0f, 0.2f, 1f, 0.2f)
        scaleAnimation.duration = 1200//设置动画变化的持续时间
        scaleAnimation.isFillEnabled = true//使其可以填充效果从而不回到原地
        scaleAnimation.fillAfter = true//不回到起始位置

        scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                //dismissAllowingStateLoss()
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })

        wrapDialogContent.startAnimation(scaleAnimation)
    }
}