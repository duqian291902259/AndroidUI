package site.duqian.android_ui.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout
import site.duqian.android_ui.R


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var mTagFlowLayout: TagFlowLayout
    private var mUITitles: MutableList<String> = mutableListOf()//emptyList<String>()
    private var mAdapter: TagAdapter<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTagFlowLayout = view.findViewById(R.id.tag_flow_layout)
    }

    private fun initTagFlowLayout() {
        mTagFlowLayout.adapter = object : TagAdapter<String>(mUITitles) {
            override fun getView(parent: FlowLayout?, position: Int, s: String?): View? {
                val tv = LayoutInflater.from(activity).inflate(
                    R.layout.main_tag_textview,
                    mTagFlowLayout, false
                ) as TextView
                tv.text = s
                return tv
            }
        }.also { mAdapter = it }

        mAdapter?.setSelectedList(0)

        mTagFlowLayout.setOnTagClickListener { view, position, parent ->
            val text = mUITitles[position]
            Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
            handleTextClicked(text)
            true
        }
    }

    private fun handleTextClicked(text: String) {

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.mData.observe(this, Observer {
            this.mUITitles.clear()
            this.mUITitles.addAll(it)

            initTagFlowLayout()
        })
    }

}