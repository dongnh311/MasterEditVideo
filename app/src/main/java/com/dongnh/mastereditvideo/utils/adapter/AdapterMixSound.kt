package com.dongnh.mastereditvideo.utils.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dongnh.mastereditvideo.R
import com.dongnh.mastereditvideo.const.MEDIA_TYPE_VIDEO
import com.dongnh.mastereditvideo.databinding.ItemMixSoundBinding
import com.dongnh.mastereditvideo.model.MixSoundModel

/**
 * Project : MasterEditVideo
 * Created by DongNH on 17/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class AdapterMixSound : RecyclerView.Adapter<AdapterMixSound.ItemViewHolder>() {

    val dataList = mutableListOf<MixSoundModel>()

    private var countMusic = 0
    private var countVideo = 0

    /**
     * Reset count item in view
     */
    fun resetCountItem() {
        countMusic = 0
        countVideo = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: ItemMixSoundBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_mix_sound,
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
        val binding: ItemMixSoundBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(itemMixSoundModel: MixSoundModel, position: Int) {

            // View type
            if (itemMixSoundModel.type.contains(MEDIA_TYPE_VIDEO)) {
                binding.viewIsVideo.visibility = View.VISIBLE
                binding.viewTrackVideo.visibility = View.GONE
                binding.viewNumber.visibility = View.GONE
                binding.viewNumber.text = countVideo.toString()
                binding.viewTrackVideo.setImageResource(R.drawable.ic_media)
                countVideo++
            } else {
                binding.viewIsVideo.visibility = View.GONE
                binding.viewTrackVideo.visibility = View.VISIBLE
                binding.viewNumber.visibility = View.VISIBLE
                countMusic++
                binding.viewTrackVideo.setImageResource(R.drawable.ic_music)
                binding.viewNumber.text = countMusic.toString()
            }

            // View progress
            binding.seerVolume.progress = (itemMixSoundModel.volume * 100).toInt()
            binding.viewVolume.text = (itemMixSoundModel.volume * 100).toInt().toString()

            // Seek change
            binding.seerVolume.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    itemMixSoundModel.volume = (progress / 100f)
                    binding.viewVolume.text = progress.toString()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            })
        }
    }
}