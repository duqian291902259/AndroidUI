package site.duqian.android_ui.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import site.duqian.android_ui.R

/**
 * description:自定义dialog
 * @author 杜小菜 Created on 6/30/21 - 10:02 PM.
 * E-mail:duqian2010@gmail.com
 */
class CommonDialogFragment : BaseDialogFragment() {

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
        val layout = inflater.inflate(R.layout.fragment_common_dialog, null);
        return layout
    }
}