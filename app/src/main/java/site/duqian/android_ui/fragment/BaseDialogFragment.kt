package site.duqian.android_ui.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment


/**
 * Description:Dialog基类
 * @author 杜小菜,Created on 6/30/21 - 10:12 PM.
 * E-mail:duqian2010@gmail.com
 */
abstract class BaseDialogFragment : DialogFragment() {
    /**
     * 监听弹出窗是否被取消
     */
    var mCancelListener: OnDialogCancelListener? = null

    interface OnDialogCancelListener {
        fun onCancel()
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return getDialog(activity) ?: return super.onCreateDialog(savedInstanceState)
    }

    abstract fun getDialog(context: Context?): Dialog?


    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            //在5.0以下的版本会出现白色背景边框，若在5.0以上设置则会造成文字部分的背景也变成透明
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                //目前只有这两个dialog会出现边框
                if (dialog is ProgressDialog || dialog is DatePickerDialog) {
                    getDialog()?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            }
            val window: Window? = getDialog()?.window
            val windowParams: WindowManager.LayoutParams? = window?.attributes
            windowParams?.dimAmount = 0.0f
            window?.attributes = windowParams
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        mCancelListener?.onCancel()
    }

    override fun onAttach(context: Context) {
        val window: Window? = activity?.window
        val attributes: WindowManager.LayoutParams? = window?.attributes
        attributes?.width = 560
        attributes?.height = 800
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //注意此处
        window?.attributes = attributes
        super.onAttach(context)
    }

}