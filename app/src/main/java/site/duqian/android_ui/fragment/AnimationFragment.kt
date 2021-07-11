package site.duqian.android_ui.fragment

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import site.duqian.android_ui.R
import site.duqian.android_ui.view.LoveAnimation

/**
 * description:各种各样的Android动画
 * @author 杜小菜 Created on 6/13/21 - 11:19 AM.
 * E-mail:duqian2010@gmail.com
 */
class AnimationFragment : BaseFragment() {

    private lateinit var mRootView: RelativeLayout
    private lateinit var mIvTestImage: ImageView
    private lateinit var mTvTitle: View


    override fun getLayoutId(): Int {
        return R.layout.fragment_animation
    }

    override fun initView(view: View) {
        mRootView = view.findViewById(R.id.rl_anim_root_view)
        mIvTestImage = view.findViewById(R.id.iv_test_image)
        mTvTitle = view.findViewById(R.id.tv_title)

        mIvTestImage.setOnClickListener {
            startLoveAnimation()
        }

        mTvTitle.setOnClickListener {
            startLoveAnimation()
        }
    }

    private fun startLoveAnimation() {
        Toast.makeText(context, "心心动画", Toast.LENGTH_LONG).show()

        val anim = LoveAnimation(mRootView, mIvTestImage, mTvTitle)
        anim.startAnimation()
    }

    override fun initData() {

    }

    companion object {
        fun newInstance() = AnimationFragment()
    }
}