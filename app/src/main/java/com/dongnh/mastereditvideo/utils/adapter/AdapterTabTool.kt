package com.dongnh.mastereditvideo.utils.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dongnh.mastereditvideo.model.TabModel
import com.dongnh.mastereditvideo.utils.interfaces.OnSpecialItemListener
import com.dongnh.mastereditvideo.view.effect.EffectFragment
import com.dongnh.mastereditvideo.view.filter.FilterFragment
import com.dongnh.mastereditvideo.view.graph.GraphFragment
import com.dongnh.mastereditvideo.view.special.SpecialFragment
import com.dongnh.mastereditvideo.view.transition.TransitionFragment

/**
 * Project : MasterEditVideo
 * Created by DongNH on 17/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class AdapterTabTool(
    fragmentManager: FragmentActivity,
    private val onSpecialItemListener: OnSpecialItemListener
) : FragmentStateAdapter(fragmentManager) {

    var dataList: MutableList<TabModel> = mutableListOf()

    /**
     * override getItemCount
     */
    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                FilterFragment.onSpecialItemListener = onSpecialItemListener
                return FilterFragment()
            }
            1 -> {
                EffectFragment.onSpecialItemListener = onSpecialItemListener
                return EffectFragment()
            }
            2 -> {
                GraphFragment.onSpecialItemListener = onSpecialItemListener
                return GraphFragment()
            }
            3 -> {
                TransitionFragment.onSpecialItemListener = onSpecialItemListener
                return TransitionFragment()
            }
            4 -> {
                SpecialFragment.onSpecialItemListener = onSpecialItemListener
                return SpecialFragment()
            }
            else -> {
                return Fragment()
            }
        }
    }

    fun updateNewItems(newItems: MutableList<TabModel>?) {
        if (newItems != null) {
            this@AdapterTabTool.dataList = newItems
        }
        this@AdapterTabTool.notifyItemRangeChanged(0, this@AdapterTabTool.dataList.size)
    }
}