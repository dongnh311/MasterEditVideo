package com.dongnh.mastereditvideo.view.main

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.dongnh.masteredit.manager.ManagerPlayerMedia
import com.dongnh.masteredit.model.MediaObject
import com.dongnh.masteredit.utils.interfaces.VideoEventLister
import com.dongnh.mastereditvideo.R
import com.dongnh.mastereditvideo.const.*
import com.dongnh.mastereditvideo.databinding.ActivityMainBinding
import com.dongnh.mastereditvideo.singleton.MyDataSingleton
import com.dongnh.mastereditvideo.utils.control.DurationControl
import com.dongnh.mastereditvideo.utils.exts.checkPermissionStorage
import com.dongnh.mastereditvideo.utils.interfaces.OnDurationTrackScrollListener
import com.dongnh.mastereditvideo.view.pickmedia.MediaPickActivity
import kotlinx.coroutines.launch
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    // Check video is playing
    private var isPlaying = false

    // Main binding
    lateinit var mainBinding: ActivityMainBinding

    // Control view duration text for video
    lateinit var durationControl: DurationControl

    // Manager player
    lateinit var managerPlayerControl: ManagerPlayerMedia

    // Find duration of video
    private val handleTask = Handler(Looper.myLooper()!!)

    // Update scroll to view
    private var runnableDurationTimeLine: Runnable? = null

    // Save list object
    private val listMedia = mutableListOf<MediaObject>()

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

        // Init tag for default
        this@MainActivity.mainBinding.btnPlay.tag = VIDEO_IS_PLAY

        // Init manager
        managerPlayerControl = ManagerPlayerMedia(this@MainActivity, mainBinding.viewPlayer)

        // Event of player
        configPlayerControl()

        // Init handler duration
        initHandleDuration()

        // Event
        configEventOfButtonLayout()

        // Back on navigation bar
        configBackPressed()

        // Observer
        configObserverData()

        // Change duration in view
        configEventSeerDuration()

        // Lister play event
        configPlayerControl()
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

        // Button play and pause
        mainBinding.btnPlay.setOnClickListener {
            if (this@MainActivity.mainBinding.btnPlay.tag == null || this@MainActivity.mainBinding.btnPlay.tag == VIDEO_IS_PLAY) {
                setupButtonPlay()
            } else {
                setupButtonPause()
            }
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
                    if (duration > this@MainActivity.managerPlayerControl.durationOfVideoProject) {
                        durationSeek =
                            this@MainActivity.managerPlayerControl.durationOfVideoProject
                    }
                    this@MainActivity.durationControl.setDurationSeek(durationSeek)
                    this@MainActivity.managerPlayerControl.seekVideoDuration(durationSeek)
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
     * Listener player event
     */
    private fun configPlayerControl() {
        // Event of player
        this@MainActivity.managerPlayerControl.videoEventLister =
            object : VideoEventLister {
                override fun onPlayWithProgress(adjDuration: Long) {
                    this@MainActivity.durationControl.setDuration(adjDuration)
                }

                override fun onPlayOverEnd() {
                    this@MainActivity.moveVideoPlayToStart()
                }
            }
    }

    /**
     * Need method reset play to start
     */
    private fun moveVideoPlayToStart() {
        setupButtonPause()

        // Check duration current, if is start, we no need srcoll to start
        if (this@MainActivity.mainBinding.viewTimeLine.currentDurationInView != 0.0) {
            Timber.e("========= Move to start play ============")
            this@MainActivity.durationControl.resetValueOfDuration()
            this@MainActivity.mainBinding.viewTimeLine.scrollToStart()
            this@MainActivity.managerPlayerControl.seekVideoDuration(0L)
        }
    }

    /**
     * Setup play video
     */
    private fun setupButtonPlay() {
        // Play video
        Timber.e("========= Play ============")
        this@MainActivity.mainBinding.btnPlay.tag = VIDEO_IS_PAUSE
        this@MainActivity.mainBinding.btnPlay.setImageResource(R.drawable.ic_pause)

        handleTask.removeCallbacksAndMessages(null)

        this@MainActivity.isPlaying = true
        this@MainActivity.managerPlayerControl.playVideo()

        runnableDurationTimeLine?.let { handleTask.postDelayed(it, 10) }
    }

    /**
     * Setup pause video
     */
    private fun setupButtonPause() {
        // Pause or stop
        Timber.e("========= Pause play ============")
        lifecycleScope.launch {
            this@MainActivity.managerPlayerControl.pauseAllPlay()
            this@MainActivity.handleTask.removeCallbacksAndMessages(null)
            runnableDurationTimeLine?.let { this@MainActivity.handleTask.removeCallbacks(it) }
            this@MainActivity.isPlaying = false
            this@MainActivity.mainBinding.btnPlay.setImageResource(R.drawable.ic_play)
            this@MainActivity.mainBinding.btnPlay.tag = VIDEO_IS_PLAY
        }
    }

    /**
     * Observer data change for progress view
     */
    private fun configObserverData() {
        // Check data is add
        MyDataSingleton.isAddNewMedia.observe(this) {
            if (it) {
                this@MainActivity.listMedia.addAll(MyDataSingleton.listMediaPick)
                this@MainActivity.mainBinding.viewTimeLine.addMediaAndCreateItemView(this@MainActivity.listMedia)
                this@MainActivity.managerPlayerControl.addMediasToPlayerQueue(this@MainActivity.listMedia)
                MyDataSingleton.listMediaPick.clear()
            }
        }
    }

    /**
     * Make duration auto play
     */
    private fun initHandleDuration() {
        this@MainActivity.runnableDurationTimeLine = Runnable {
            this@MainActivity.mainBinding.viewTimeLine.updateScrollOfMainView()
            this@MainActivity.runnableDurationTimeLine?.let {
                if (this@MainActivity.isPlaying) {handleTask.postDelayed(it, 10)} else handleTask.removeCallbacks(it)
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