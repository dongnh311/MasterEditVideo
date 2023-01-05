package com.dongnh.mastereditvideo.view.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.dongnh.mastereditvideo.R
import com.dongnh.mastereditvideo.const.MEDIA_IMAGE
import com.dongnh.mastereditvideo.const.MEDIA_VIDEO
import com.dongnh.mastereditvideo.const.NAME_SEND_PICK_MEDIA
import com.dongnh.mastereditvideo.databinding.ActivityMainBinding
import com.dongnh.mastereditvideo.singleton.MyDataSingleton
import com.dongnh.mastereditvideo.utils.control.DurationControl
import com.dongnh.mastereditvideo.utils.exts.checkPermissionStorage
import com.dongnh.mastereditvideo.utils.interfaces.OnDurationTrackScrollListener
import com.dongnh.mastereditvideo.view.pickmedia.MediaPickActivity


class MainActivity : AppCompatActivity() {

    // Check video is playing
    private var isPlaying = false

    // Main binding
    lateinit var mainBinding: ActivityMainBinding

    // Control view duration text for video
    lateinit var durationControl: DurationControl

    // Request permission for storage
    private val requestPermissionLauncherStorage =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permission ->
            var isGranted = true
            permission.forEach {
                if (!it.value) {
                    isGranted = false
                    return@forEach
                }
            }
            if (!isGranted) {
                // Show dialog can't continue
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        durationControl = DurationControl(mainBinding.viewDuration)

        // Request permission first
        if (!checkPermissionStorage()) {
            requestPermissionLauncherStorage.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
            )
        }

        // Event
        configEventOfButtonLayout()

        // Back on navigation bar
        configBackPressed()

        // Observer
        configObserverData()

        // Change duration in view
        configEventSeerDuration()
    }

    /**
     * Event of button in layout
     */
    private fun configEventOfButtonLayout() {
        // Open pick image
        mainBinding.btnImage.setOnClickListener {
            val intent = Intent(this@MainActivity, MediaPickActivity::class.java)
            intent.putExtra(NAME_SEND_PICK_MEDIA, MEDIA_IMAGE)
            startActivity(intent)
        }

        // Open pick video
        mainBinding.btnVideo.setOnClickListener {
            val intent = Intent(this@MainActivity, MediaPickActivity::class.java)
            intent.putExtra(NAME_SEND_PICK_MEDIA, MEDIA_VIDEO)
            startActivity(intent)
        }
    }

    /**
     * Seer time duration view
     */
    private fun configEventSeerDuration() {
        mainBinding.viewTimeLine.onDurationTrackScrollListener = object : OnDurationTrackScrollListener {
            override fun onDurationSeekChange(duration: Long) {
                if (!this@MainActivity.isPlaying) {
                    var durationSeek = duration
                    // Todo : Update to player manager
                    this@MainActivity.durationControl.setDurationSeek(durationSeek)
                }
            }

            override fun onScrollXOfView(duration: Long) {
                if (!this@MainActivity.isPlaying) {
                    this@MainActivity.mainBinding.viewTimeLine.currentDurationInView =
                        duration.toDouble()

                    if (duration == 0L) {
                        this@MainActivity.mainBinding.viewTimeLine.isScrollToStart =
                            false
                    }
                }
            }
        }
    }

    /**
     * Observer data change for progress view
     */
    private fun configObserverData() {
        // Check data is add
        MyDataSingleton.isAddNewMedia.observe(this) {
            if (it) {
                // Add new media
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MyDataSingleton.resetValue()
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

}