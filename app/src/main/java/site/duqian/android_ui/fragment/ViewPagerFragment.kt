package site.duqian.android_ui.fragment

import android.view.View
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import site.duqian.android_ui.view.pageTransformer.ScaleInTransformer
import site.duqian.android_ui.R
import site.duqian.android_ui.utils.dp
import site.duqian.android_ui.view.pageTransformer.ZoomOutPageTransformer

/**
 * description:ViewPager转场效果
 * @author 杜小菜 Created on 7/14/21 - 10:41 PM.
 * E-mail:duqian2010@gmail.com
 */
class ViewPagerFragment : BaseFragment() {
    private lateinit var mTvTitle: View
    private lateinit var mViewPager: ViewPager2
    private var fragmentList: MutableList<BaseFragment>? = null

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
        fragmentList = ArrayList()
        fragmentList!!.add(AnimationFragment())
        fragmentList!!.add(ImageEffectFragment())
        fragmentList!!.add(ImageEffectFragment())
        fragmentList!!.add(ImageEffectFragment())
        fragmentList!!.add(ProgressFragment())

        val adapter: MyFragmentStateAdapter? =
            activity?.let { MyFragmentStateAdapter(it, fragmentList!!) }
        mViewPager.adapter = adapter
        mViewPager.setPageTransformer(ScaleInTransformer())//设置页面切换的动画
        //mViewPager.setPageTransformer(ZoomOutPageTransformer())//设置页面切换的动画
        mViewPager.isUserInputEnabled = true //false ，禁用页面滑动

        mViewPager.currentItem = 2
        mViewPager.offscreenPageLimit = 3

        val lp = mViewPager.layoutParams as RelativeLayout.LayoutParams
        mViewPager.layoutParams = lp
        val margin2 = 20.dp.toInt()
        mViewPager.setPadding(margin2,10,margin2,10)
        mViewPager.clipToPadding = false
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