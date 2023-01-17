package com.dongnh.mastereditvideo.view.transition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dongnh.masteredit.const.SPECIAL_TYPE_TRANSITION
import com.dongnh.masteredit.model.SpecialModel
import com.dongnh.mastereditvideo.R
import com.dongnh.mastereditvideo.databinding.FragmentTransitionBinding
import com.dongnh.mastereditvideo.utils.adapter.AdapterSpecial
import com.dongnh.mastereditvideo.utils.interfaces.OnSpecialItemListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.nio.charset.Charset

/**
 * Project : MasterEditVideo
 * Created by DongNH on 17/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class TransitionFragment : Fragment() {
    lateinit var dataBinding: FragmentTransitionBinding

    // Adapter for view
    var adapterSpecial = AdapterSpecial()

    companion object {
        var onSpecialItemListener: OnSpecialItemListener? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_transition,
            container,
            false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handelViewCreated()
    }

    private fun handelViewCreated() {
        this@TransitionFragment.dataBinding.recyclerView.adapter = adapterSpecial

        // Create data for filter
        var filterList: ArrayList<SpecialModel> = arrayListOf()

        val stream =
            this@TransitionFragment.requireContext().assets?.open("transition/transitions.json")
        val size = stream?.available()
        if (size != null) {
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()

            filterList =
                Gson().fromJson(
                    String(buffer, Charset.forName("UTF-8")),
                    object : TypeToken<ArrayList<SpecialModel>>() {}.type
                )
        }

        // Set type
        filterList.forEach {
            it.type = SPECIAL_TYPE_TRANSITION
        }

        adapterSpecial.dataList.clear()
        adapterSpecial.dataList.addAll(filterList)
        adapterSpecial.notifyItemRangeInserted(0, filterList.size)

        adapterSpecial.onSpecialItemListener = object : OnSpecialItemListener {
            override fun onItemSpecialTouchDown(itemSpecial: SpecialModel, position: Int) {
                Timber.e("onItemSpecialTouchDown")
                TransitionFragment.onSpecialItemListener?.onItemSpecialTouchDown(
                    itemSpecial,
                    position
                )
            }

            override fun onItemSpecialTouchUp(itemSpecial: SpecialModel, position: Int) {
                Timber.e("onItemSpecialTouchUp")
                TransitionFragment.onSpecialItemListener?.onItemSpecialTouchUp(
                    itemSpecial,
                    position
                )
            }

            override fun onItemSpecialClick(itemSpecial: SpecialModel, position: Int) {
                Timber.e("onItemSpecialClick")
                TransitionFragment.onSpecialItemListener?.onItemSpecialClick(itemSpecial, position)
            }
        }
    }
}