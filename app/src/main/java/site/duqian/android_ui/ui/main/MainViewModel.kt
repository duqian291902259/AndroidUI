package site.duqian.android_ui.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    var mData: MutableLiveData<List<String>> = MutableLiveData()

    private val mUITitles = arrayOf(
        "弹窗",
        "Android",
        "Button ImageView",
        "TextView",
        "Helloworld",
        "Android",
        "Weclome Hello",
        "Button Text",
        "TextView"
    )

    init {
        mData.value = mUITitles.toList()
    }
}