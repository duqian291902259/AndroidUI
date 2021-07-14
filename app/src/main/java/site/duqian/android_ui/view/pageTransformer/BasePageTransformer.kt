package com.huya.nimo.home.ui.widget.banner

import android.annotation.TargetApi
import android.os.Build
import android.view.View
import androidx.viewpager.widget.ViewPager.PageTransformer
import androidx.viewpager2.widget.ViewPager2

/**
 * description:PageTransformer基类
 *
 * @author 杜小菜 Created on 2021/7/14 - 13:14.
 * E-mail:duqian2010@gmail.com
 */
abstract class BasePageTransformer : ViewPager2.PageTransformer {
    @JvmField
    protected var mPageTransformer = NonPageTransformer.INSTANCE

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun transformPage(view: View, position: Float) {
        mPageTransformer.transformPage(view, position)
        pageTransform(view, position)
    }

    protected abstract fun pageTransform(view: View?, position: Float)

    companion object {
        const val DEFAULT_CENTER = 0.5f
    }
}