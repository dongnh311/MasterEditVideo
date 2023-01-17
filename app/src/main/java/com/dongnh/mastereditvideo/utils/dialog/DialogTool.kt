package com.dongnh.mastereditvideo.utils.dialog

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.dongnh.mastereditvideo.R
import com.dongnh.mastereditvideo.databinding.DialogToolBinding
import com.dongnh.mastereditvideo.model.TabModel
import com.dongnh.mastereditvideo.utils.adapter.AdapterTabTool
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Project : MasterEditVideo
 * Created by DongNH on 17/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class DialogTool(private val context: FragmentActivity) {
    // Dialog alert
    var alertDialog: AlertDialog? = null

    var dataBinding: DialogToolBinding

    var adapterTab = AdapterTabTool(context)

    var currentPageSelect = -1

    init {
        val builder = AlertDialog.Builder(this@DialogTool.context, R.style.customDialogTheme)
        this@DialogTool.dataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.dialog_tool, null, false
        )

        this@DialogTool.dataBinding.root.let { builder.setView(it) }

        this@DialogTool.alertDialog = builder.create()
        this@DialogTool.alertDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this@DialogTool.alertDialog?.setCancelable(true)
        val tabModel = TabModel()
        adapterTab.updateNewItems(tabModel.createTabModels())
    }

    /**
     * Show dialog tool
     */
    fun showDialogTool() {
        // View tab
        // Config adapter
        this@DialogTool.dataBinding.pagerViewFragment.adapter = adapterTab
        // Max limit view init
        dataBinding.pagerViewFragment.offscreenPageLimit = 4

        // Config icon and text for tab
        TabLayoutMediator(
            dataBinding.tabSelect,
            dataBinding.pagerViewFragment,
            false,
            false
        ) { tab, position ->
            dataBinding.pagerViewFragment.setCurrentItem(tab.position, false)
            adapterTab.dataList[position].nameTab?.let {
                tab.text = it
            }

            when (position) {
                0 -> {
                    tab.icon =
                        ContextCompat.getDrawable(this@DialogTool.context, R.drawable.ic_filter)
                }
                1 -> {
                    tab.icon =
                        ContextCompat.getDrawable(this@DialogTool.context, R.drawable.ic_effect)
                }
                2 -> {
                    tab.icon =
                        ContextCompat.getDrawable(this@DialogTool.context, R.drawable.ic_graph)
                }
                3 -> {
                    tab.icon =
                        ContextCompat.getDrawable(this@DialogTool.context, R.drawable.ic_transition)
                }
                4 -> {
                    tab.icon =
                        ContextCompat.getDrawable(this@DialogTool.context, R.drawable.ic_special)
                }
            }

        }.attach()

        // Don't allow swipe to next fragment
        dataBinding.pagerViewFragment.isUserInputEnabled = false
        dataBinding.pagerViewFragment.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            @Suppress("DEPRECATION")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Current page
                currentPageSelect = position
            }
        })

        // Show dialog
        this@DialogTool.alertDialog?.show()
    }
}