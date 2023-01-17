package com.dongnh.mastereditvideo.utils.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dongnh.masteredit.model.SpecialModel
import com.dongnh.mastereditvideo.R
import com.dongnh.mastereditvideo.const.CLICK_ACTION_THRESHOLD
import com.dongnh.mastereditvideo.databinding.ItemSpecialBinding
import com.dongnh.mastereditvideo.utils.interfaces.OnSpecialItemListener

/**
 * Project : MasterEditVideo
 * Created by DongNH on 17/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class AdapterSpecial : RecyclerView.Adapter<AdapterSpecial.ItemViewHolder>() {

    // Data list
    val dataList = mutableListOf<SpecialModel>()

    // Call back
    var onSpecialItemListener: OnSpecialItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: ItemSpecialBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_special,
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(dataList[position], position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ItemViewHolder(
        val binding: ItemSpecialBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: SpecialModel, position: Int) {
            // View thumb
            if (binding.filterThumbnail.tag == null || item.thumbnail != binding.filterThumbnail.tag) {
                Glide.with(binding.filterThumbnail.context).clear(binding.filterThumbnail)
                Glide.with(binding.filterThumbnail.context)
                    .load(Uri.parse("file:///android_asset/" + item.thumbnail))
                    .into(binding.filterThumbnail)
            }

            // View name
            binding.filterName.text = item.name

            // Touch
            binding.parentView.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        onSpecialItemListener?.onItemSpecialTouchDown(item, position)
                    }
                    MotionEvent.ACTION_UP -> {
                        if (event.eventTime - event.downTime < CLICK_ACTION_THRESHOLD) {
                            onSpecialItemListener?.onItemSpecialClick(item, position)
                        } else {
                            onSpecialItemListener?.onItemSpecialTouchUp(item, position)
                        }
                    }
                }
                return@setOnTouchListener true
            }

            // Click
            binding.parentView.setOnClickListener {
                onSpecialItemListener?.onItemSpecialClick(item, position)
            }
        }
    }
}