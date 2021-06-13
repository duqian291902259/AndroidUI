package site.duqian.android_ui.fragment

import android.view.View
import site.duqian.android_ui.R

/**
 * description:图片融合效果，比如梯形图片，圆角图片
 * @author 杜小菜 Created on 6/13/21 - 11:20 AM.
 * E-mail:duqian2010@gmail.com
 */
class ImageEffectFragment : BaseFragment() {

    companion object {
        fun newInstance() = ImageEffectFragment()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_image_effect
    }

    override fun initView(view: View) {

    }

    override fun initData() {

    }
}