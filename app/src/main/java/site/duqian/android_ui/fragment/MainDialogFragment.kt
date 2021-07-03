package site.duqian.android_ui.fragment

import android.view.View
import site.duqian.android_ui.R

/**
 * description:各种各样的dialog
 * @author 杜小菜 Created on 6/13/21 - 11:19 AM.
 * E-mail:duqian2010@gmail.com
 */
class MainDialogFragment : BaseFragment() {

    private lateinit var wrapDialogContent: View
    private lateinit var tvTitle: View
    companion object {
        fun newInstance() = MainDialogFragment()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_common_dialog
    }

    override fun initView(view: View) {
        wrapDialogContent = view.findViewById(R.id.wrap_dialog_content)
        tvTitle = view.findViewById(R.id.tv_title)

        wrapDialogContent.setOnClickListener{
            showDialog()
        }
    }

    override fun initData() {
        showDialog()
    }

    private fun showDialog() {
        val dialog = CommonDialogFragment.newInstance(true, null)
        if (fragmentManager != null) {
            dialog.show(fragmentManager!!, "dialog")
        }
    }
}