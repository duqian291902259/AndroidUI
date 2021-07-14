package com.huya.nimo.home.ui.widget.banner

import android.view.View
import androidx.viewpager.widget.ViewPager.PageTransformer

/**
 * description:
 *
 * @author 杜小菜 Created on 2021/7/15 - 13:14.
 * E-mail:duqian2010@gmail.com
 */
class NonPageTransformer : PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.scaleX = 0.999f //hack
    }

    companion object {
        @JvmField
        val INSTANCE: PageTransformer = NonPageTransformer()
    }
}