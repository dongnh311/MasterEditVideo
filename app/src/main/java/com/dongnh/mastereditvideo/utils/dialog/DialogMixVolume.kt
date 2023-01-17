package com.dongnh.mastereditvideo.utils.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dongnh.masteredit.model.MediaModel
import com.dongnh.masteredit.model.MusicModel
import com.dongnh.mastereditvideo.R
import com.dongnh.mastereditvideo.const.MEDIA_TYPE_MUSIC
import com.dongnh.mastereditvideo.const.MEDIA_TYPE_VIDEO
import com.dongnh.mastereditvideo.databinding.DialogMixVolumeBinding
import com.dongnh.mastereditvideo.model.MixSoundModel
import com.dongnh.mastereditvideo.utils.adapter.AdapterMixSound
import com.dongnh.mastereditvideo.utils.interfaces.OnConfirmClickMixSound

/**
 * Project : MasterEditVideo
 * Created by DongNH on 17/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class DialogMixVolume(private val context: Context) {

    // Dialog alert
    var alertDialog: AlertDialog? = null

    lateinit var dataBinding: DialogMixVolumeBinding

    // Adapter volume
    private val adapterMixSound = AdapterMixSound()

    // Callback
    var onConfirmClickMixSound: OnConfirmClickMixSound? = null

    init {
        val builder = AlertDialog.Builder(this@DialogMixVolume.context, R.style.customDialogTheme)
        this@DialogMixVolume.dataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.dialog_mix_volume, null, false
        )

        this@DialogMixVolume.dataBinding.root.let { builder.setView(it) }

        this@DialogMixVolume.alertDialog = builder.create()
        this@DialogMixVolume.alertDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this@DialogMixVolume.alertDialog?.setCancelable(false)
    }

    /**
     * Show dialog mix sound
     */
    fun showDialogMixVolume(
        listMedia: MutableList<MediaModel>,
        listMusic: MutableList<MusicModel>
    ) {
        val listItemMenu = LinearLayoutManager(this@DialogMixVolume.context)
        listItemMenu.orientation = LinearLayoutManager.HORIZONTAL
        this@DialogMixVolume.dataBinding.viewItems.layoutManager = listItemMenu
        this@DialogMixVolume.dataBinding.viewItems.adapter = adapterMixSound

        // Reset old value
        adapterMixSound.resetCountItem()

        // Create data to view
        val listToView = mutableListOf<MixSoundModel>()
        listMedia.forEach { mediaModel ->
            if (mediaModel.mediaType == MEDIA_TYPE_VIDEO) {
                val mixSoundModel = MixSoundModel()
                mixSoundModel.itemId = mediaModel.mediaId
                mixSoundModel.type = MEDIA_TYPE_VIDEO
                mixSoundModel.volume = mediaModel.volume

                listToView.add(mixSoundModel)
            }
        }

        // Add music
        listMusic.forEachIndexed { index, musicModel ->
            val mixSoundModel = MixSoundModel()
            mixSoundModel.itemId = musicModel.id
            mixSoundModel.type = MEDIA_TYPE_MUSIC
            mixSoundModel.volume = musicModel.volume
            mixSoundModel.indexMusic = index

            listToView.add(mixSoundModel)
        }

        // Make it on view
        val count = adapterMixSound.dataList.size
        adapterMixSound.dataList.clear()
        adapterMixSound.notifyItemRangeRemoved(0, count)
        adapterMixSound.dataList.addAll(listToView)
        adapterMixSound.notifyItemRangeInserted(0, listToView.size)

        // Button cancel
        dataBinding.btnCancel.setOnClickListener {
            this@DialogMixVolume.alertDialog?.dismiss()
        }

        // Button ok
        dataBinding.btnConfirm.setOnClickListener {
            onConfirmClickMixSound?.onConfirmClick(adapterMixSound.dataList)
        }

        // Show dialog
        this@DialogMixVolume.alertDialog?.show()
    }
}