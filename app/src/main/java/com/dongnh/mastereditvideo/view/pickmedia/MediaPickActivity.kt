package com.dongnh.mastereditvideo.view.pickmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import com.dongnh.mastereditvideo.R
import com.dongnh.mastereditvideo.const.MEDIA_IMAGE
import com.dongnh.mastereditvideo.const.NAME_SEND_PICK_MEDIA
import com.dongnh.mastereditvideo.databinding.ActivityMediaPickBinding
import com.dongnh.masteredit.model.MediaObject
import com.dongnh.mastereditvideo.singleton.MyDataSingleton
import com.dongnh.mastereditvideo.utils.adapter.AdapterMedia
import com.dongnh.mastereditvideo.utils.exts.checkPermissionStorage
import com.dongnh.mastereditvideo.utils.interfaces.OnImageClick

class MediaPickActivity : AppCompatActivity() {

    lateinit var activityMediaPickBinding: ActivityMediaPickBinding

    private var mediaModeSelect = MEDIA_IMAGE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMediaPickBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_media_pick)

        if (intent != null && intent.extras != null) {
            mediaModeSelect = intent.extras?.getInt(NAME_SEND_PICK_MEDIA, MEDIA_IMAGE)!!

            // Change title
            if (mediaModeSelect != MEDIA_IMAGE) {
                activityMediaPickBinding.headerTitle.text = getString(R.string.media_pick_video)
            }
        }

        initEventForView()
        initAdapter()

        // Back button click
        configBackPressed()
    }

    // Back to preview
    private fun configBackPressed() {
        onBackPressedDispatcher.addCallback(
            this /* lifecycle owner */,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            })
    }

    // Event of view
    private fun initEventForView() {
        activityMediaPickBinding.btnBack.setOnClickListener {
            MyDataSingleton.listMediaPick.clear()
            this@MediaPickActivity.finish()
        }

        activityMediaPickBinding.btnNext.setOnClickListener {
            // Send back to main
            MyDataSingleton.isAddNewMedia.value = true
            this@MediaPickActivity.finish()
        }
    }

    /**
     * Make adapter load and show media
     */
    private fun initAdapter() {
        if (!checkPermissionStorage()) {
            this.finish()
        } else {
            val adapterMedia = AdapterMedia()
            adapterMedia.getAllShownImagesPath(this, mediaModeSelect)

            if (adapterMedia.arrayImage.size == 0) {
                activityMediaPickBinding.gridView.visibility = View.GONE
                activityMediaPickBinding.viewEmpty.visibility = View.VISIBLE
            } else {
                activityMediaPickBinding.gridView.visibility = View.VISIBLE
                activityMediaPickBinding.viewEmpty.visibility = View.GONE
            }

            adapterMedia.onImageClick = object : OnImageClick {
                override fun imageClick(imageObject: MediaObject) {
                    MyDataSingleton.listMediaPick.add(imageObject)
                    activityMediaPickBinding.btnNext.visibility = View.VISIBLE
                }

                override fun imageClear(imageObject: MediaObject) {
                    MyDataSingleton.listMediaPick.remove(imageObject)
                    // Show or hide button next
                    if (MyDataSingleton.listMediaPick.isEmpty()) {
                        activityMediaPickBinding.btnNext.visibility = View.GONE
                    } else {
                        activityMediaPickBinding.btnNext.visibility = View.VISIBLE
                    }
                }
            }

            activityMediaPickBinding.gridView.adapter = adapterMedia
        }
    }
}