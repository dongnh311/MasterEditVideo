package com.dongnh.mastereditvideo.utils.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dongnh.masteredit.model.MusicModel
import com.dongnh.mastereditvideo.R
import com.dongnh.mastereditvideo.databinding.ItemMusicBinding
import com.dongnh.mastereditvideo.utils.interfaces.OnMusicActionClick

/**
 * Project : MasterEditVideo
 * Created by DongNH on 12/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class AdapterMusic : RecyclerView.Adapter<AdapterMusic.ItemViewHolder>() {

    // List item
    val dataList = mutableListOf<MusicModel>()

    // Event callback
    var onMusicActionClick : OnMusicActionClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: ItemMusicBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_music,
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.isNotEmpty()) {
            val musicModel = dataList[position]
            holder.binding.progressMusic.progress = musicModel.progress
            if (musicModel.progress == 100) {
                holder.binding.viewPlay.setImageResource(R.drawable.ic_play_music)
                holder.binding.progressMusic.progress = 0
            }

            // Button download
            holder.binding.viewDownload.isEnabled = !musicModel.isDownloaded
        } else {
            // Make old method is call
            onBindViewHolder(holder, position)
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(dataList[position], position)
    }

    override fun getItemCount(): Int {
        return  dataList.size
    }

    inner class ItemViewHolder(
        val binding: ItemMusicBinding
    ) : RecyclerView.ViewHolder(binding.root)  {

        fun bind(musicModel: MusicModel, position: Int) {
            // Config select
            if (musicModel.isChoose) {
                binding.mainViewMusic.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.mainViewMusic.context,
                        R.color.colorItemMusicSelect
                    )
                )
            } else {
                binding.mainViewMusic.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.mainViewMusic.context,
                        android.R.color.transparent
                    )
                )
            }

            // Enable button download
            binding.viewDownload.isEnabled = !musicModel.isDownloaded

            // Name of music
            binding.viewName.text = musicModel.nameMusic

            // Bind image view
            if (binding.viewIcon.tag == null || binding.viewIcon.tag != musicModel.urlThumb) {
                Glide.with(binding.viewIcon.context).clear(binding.viewIcon)
                Glide.with(binding.viewIcon.context)
                    .load(musicModel.urlThumb)
                    .thumbnail(0.01f)
                    .into(binding.viewIcon)
            }

            // Icon play or pause
            if (musicModel.isPlay) {
                binding.viewPlay.setImageResource(R.drawable.ic_pause_music)
                if (binding.progressMusic.progress != musicModel.progress) {
                    binding.progressMusic.progress = musicModel.progress
                }
            } else {
                binding.viewPlay.setImageResource(R.drawable.ic_play_music)
                binding.progressMusic.progress = 0
            }

            // Background
            if (musicModel.isChoose) {
                binding.viewAuthor.setTextColor(
                    ContextCompat.getColor(
                        binding.viewIcon.context,
                        R.color.colorMusicActive
                    )
                )
                binding.viewName.setTextColor(
                    ContextCompat.getColor(
                        binding.viewIcon.context,
                        R.color.colorMusicActive
                    )
                )
                binding.viewDot.setImageResource(R.drawable.ic_music_dot_active)
            } else {
                binding.viewAuthor.setTextColor(
                    ContextCompat.getColor(
                        binding.viewIcon.context,
                        R.color.colorWhite
                    )
                )
                binding.viewName.setTextColor(
                    ContextCompat.getColor(
                        binding.viewIcon.context,
                        R.color.colorWhite
                    )
                )
                binding.viewDot.setImageResource(R.drawable.ic_music_dot)
            }

            // Event play
            binding.viewPlay.setOnClickListener {
                updateIconPlay(musicModel)
                onMusicActionClick?.onMusicPlay(musicModel, position)
            }

            // Event download
            binding.viewDownload.setOnClickListener {
                onMusicActionClick?.onMusicDownload(musicModel, position)
            }

            // Event choose
            binding.mainViewMusic.setOnClickListener {
                // Clear all select
                dataList.forEachIndexed { index, musicModel ->
                    if (index != position) {
                        musicModel.isChoose = false
                        this@AdapterMusic.notifyItemChanged(index)
                    }
                }
                musicModel.isChoose = !musicModel.isChoose
                this@AdapterMusic.notifyItemChanged(position)
                onMusicActionClick?.onMusicClick(musicModel, position)
            }

            // Name
            binding.viewAuthor.text = musicModel.nameSinger
            binding.executePendingBindings()
        }
    }

    // Update all icon if need
    private fun updateIconPlay(item: MusicModel) {
        this@AdapterMusic.dataList.forEach {
            it.isPlay = it.id == item.id && !item.isPlay
        }

        this@AdapterMusic.notifyItemChanged(0, dataList.size)
    }
}