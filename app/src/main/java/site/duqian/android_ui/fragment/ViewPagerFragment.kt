package site.duqian.android_ui.fragment

import android.view.View
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import site.duqian.android_ui.R
import site.duqian.android_ui.utils.dp
import site.duqian.android_ui.view.pageTransformer.ScaleInTransformer


/**
 * description:ViewPager转场效果
 * @author 杜小菜 Created on 7/14/21 - 10:41 PM.
 * E-mail:duqian2010@gmail.com
 */
class ViewPagerFragment : BaseFragment() {
    private lateinit var mTvTitle: View
    private lateinit var mViewPager: ViewPager2
    private var fragmentList: MutableList<BaseFragment> = ArrayList()

    companion object {
        fun newInstance() = ViewPagerFragment()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_viewpager
    }

    override fun initView(view: View) {
        mTvTitle = view.findViewById(R.id.tv_title)
        mViewPager = view.findViewById(R.id.viewPager)
    }

    override fun initData() {
        fragmentList.clear()
        fragmentList.also {
            it.add(AnimationFragment())
            it.add(ImageEffectFragment())
            it.add(ImageEffectFragment())
            it.add(ImageEffectFragment())
            it.add(ProgressFragment())
        }

        val adapter: MyFragmentStateAdapter? =
            activity?.let { MyFragmentStateAdapter(it, fragmentList) }
        mViewPager.adapter = adapter
        mViewPager.setPageTransformer(ScaleInTransformer())//设置页面切换的动画
        mViewPager.isUserInputEnabled = true //false ，禁用页面滑动

        mViewPager.currentItem = fragmentList.size / 2
        mViewPager.offscreenPageLimit = 4

        //设置左右padding
        val lp = mViewPager.layoutParams as RelativeLayout.LayoutParams
        mViewPager.layoutParams = lp
        val margin2 = 20.dp.toInt()
        mViewPager.setPadding(margin2, 10, margin2, 10)
        mViewPager.clipToPadding = false

        //设置监听实现无线滚动
        addPagerScrollListener()
    }

    private fun addPagerScrollListener() {
        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            var currentPosition = 0

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                currentPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                // SCROLL_STATE_IDLE 页面完全展现，没有动画,防止页面闪烁！
                if (state != ViewPager2.SCROLL_STATE_IDLE && fragmentList.size < 2) return

                // 当视图在第一个时，将页面号设置为图片的最后一张。
                if (currentPosition == 0) {
                    mViewPager.setCurrentItem(fragmentList.size - 2, false)
                } else if (currentPosition == fragmentList.size - 1) {
                    // 当视图在最后一个是,将页面号设置为图片的第一张。
                    mViewPager.setCurrentItem(1, false)
                }
            }
        })
    }

    class MyFragmentStateAdapter(
        fragmentActivity: FragmentActivity,
        private val list: List<BaseFragment>
    ) :
        FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): BaseFragment {
            return list[position]
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

}