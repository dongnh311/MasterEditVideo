package com.dongnh.mastereditvideo.view.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.dongnh.masteredit.const.*
import com.dongnh.masteredit.manager.ManagerPlayerMedia
import com.dongnh.masteredit.model.MediaModel
import com.dongnh.masteredit.model.MusicModel
import com.dongnh.masteredit.model.SpecialModel
import com.dongnh.masteredit.utils.exts.createMediaTransformPath
import com.dongnh.masteredit.utils.exts.deleteFolderIfExit
import com.dongnh.masteredit.utils.interfaces.VideoEventLister
import com.dongnh.mastereditvideo.R
import com.dongnh.mastereditvideo.const.*
import com.dongnh.mastereditvideo.const.MEDIA_TYPE_VIDEO
import com.dongnh.mastereditvideo.databinding.ActivityMainBinding
import com.dongnh.mastereditvideo.model.MixSoundModel
import com.dongnh.mastereditvideo.singleton.MyDataSingleton
import com.dongnh.mastereditvideo.utils.control.DurationControl
import com.dongnh.mastereditvideo.utils.dialog.DialogMixVolume
import com.dongnh.mastereditvideo.utils.dialog.DialogTool
import com.dongnh.mastereditvideo.utils.exts.checkPermissionStorage
import com.dongnh.mastereditvideo.utils.exts.isItemTransparent
import com.dongnh.mastereditvideo.utils.exts.isNotItemNone
import com.dongnh.mastereditvideo.utils.exts.isTransitionItem
import com.dongnh.mastereditvideo.utils.interfaces.*
import com.dongnh.mastereditvideo.view.pickmedia.MediaPickActivity
import com.dongnh.mastereditvideo.view.pickmusic.PickMusicFragment
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
    private val listMedia = mutableListOf<MediaModel>()

    // Save list music
    private val listMusic = mutableListOf<MusicModel>()

    // Save index click on media
    private var indexMediaSelect = -1

    // Save index item special click
    private var indexSpecialClick = -1

    // Dialog mix volume
    private val dialogMixVolume by lazy {
        DialogMixVolume(this@MainActivity)
    }

    // Dialog tool
    private val dialogTool by lazy {
        DialogTool(this@MainActivity)
    }

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

        // Delete all temp file
        deleteFolderIfExit(createMediaTransformPath(this))

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

        // Button delete
        mainBinding.btnDelete.setOnClickListener {
            if (indexMediaSelect > -1) {
                listMedia.removeAt(indexMediaSelect)
                indexMediaSelect = -1

                makeViewShowMedia()
            }
        }

        // Open dialog pick music
        mainBinding.btnMusic.setOnClickListener {
            val pickMusicFragment = PickMusicFragment()
            PickMusicFragment.onMusicSelectListener = object : OnMusicSelectListener {
                override fun onMusicChoose(musicModel: MusicModel) {
                    Timber.e("We have music and it to view")
                    this@MainActivity.listMusic.add(musicModel)

                    // Add to track
                    this@MainActivity.mainBinding.viewTimeLine.addMusicToTrackView(this@MainActivity.listMusic)

                    // Add to manager play
                    this@MainActivity.managerPlayerControl.addMusicToQueue(this@MainActivity.listMusic)
                }
            }
            pickMusicFragment.show(supportFragmentManager, "PickMusicFragment")
        }

        // Open tool
        mainBinding.btnTool.setOnClickListener {
            // Pause if it is playing
            this@MainActivity.setupButtonPause()

            // Show dialog
            dialogTool.showDialogTool(object : OnSpecialItemListener {
                override fun onItemSpecialTouchDown(itemSpecial: SpecialModel, position: Int) {
                    if (isNotItemNone(itemSpecial)) {
                        // Make start time
                        itemSpecial.beginAt = this@MainActivity.managerPlayerControl.durationPlayed

                        // Play
                        if (!this@MainActivity.isPlaying) {
                            this@MainActivity.setupButtonPlay()
                        }
                    }
                }

                override fun onItemSpecialTouchUp(itemSpecial: SpecialModel, position: Int) {
                    if (isNotItemNone(itemSpecial)) {
                        // Pause
                        if (this@MainActivity.isPlaying) {
                            this@MainActivity.setupButtonPause()
                        }

                        // Make end time
                        itemSpecial.endAt =
                            this@MainActivity.managerPlayerControl.durationPlayed

                        this@MainActivity.dialogTool.alertDialog?.dismiss()
                        Timber.e("New filter duration , start : ${itemSpecial.beginAt}, end :  ${itemSpecial.endAt}")
                        this@MainActivity.mainBinding.viewTimeLine.drawItemFilter(itemSpecial)

                        // Clone item
                        val cloneSpecial = SpecialModel().cloneItem(itemSpecial)
                        this@MainActivity.managerPlayerControl.addSpecialToPreview(cloneSpecial)
                    }
                }

                override fun onItemSpecialClick(itemSpecial: SpecialModel, position: Int) {
                    if (isItemTransparent(itemSpecial)) {
                        Timber.e("onItemSpecialClick item transparent click")
                        if (indexSpecialClick == -1) {
                            Toast.makeText(
                                this@MainActivity,
                                "Please select item special for action",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            when (itemSpecial.id) {
                                ITEM_TRANSITION_NONE -> {
                                    // Clear transition
                                    val specialModel = SpecialModel()
                                    specialModel.id = ITEM_TRANSITION_NONE
                                    specialModel.type = SPECIAL_TYPE_TRANSITION

                                    // Update to player
                                    this@MainActivity.managerPlayerControl.updateTransition(
                                        indexSpecialClick,
                                        specialModel
                                    )

                                    // Update to control
                                    this@MainActivity.mainBinding.viewTimeLine.updateViewForTransition(
                                        indexSpecialClick,
                                        specialModel
                                    )

                                    indexSpecialClick = -1
                                }
                                ITEM_FILTER_NONE -> {
                                    // Clear filter
                                    this@MainActivity.mainBinding.viewTimeLine.removeFilterAdded(
                                        indexSpecialClick,
                                        itemSpecial
                                    )
                                    this@MainActivity.managerPlayerControl.removeFilterFromPreview(
                                        indexSpecialClick
                                    )

                                    indexSpecialClick = -1
                                }
                                ITEM_EFFECT_NONE -> {

                                }
                                ITEM_GRAPH_NONE -> {

                                }
                                ITEM_SPECIAL_NONE -> {

                                }
                            }
                        }
                    } else {
                        // Transition add
                        if (indexSpecialClick == -1 || !isTransitionItem(itemSpecial)) {
                            Toast.makeText(
                                this@MainActivity,
                                "Please select transition for this",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Timber.e("We have transition : ${itemSpecial.id}")
                            // Update to player
                            this@MainActivity.managerPlayerControl.updateTransition(
                                indexSpecialClick,
                                itemSpecial
                            )

                            // Update to control
                            this@MainActivity.mainBinding.viewTimeLine.updateViewForTransition(
                                indexSpecialClick,
                                itemSpecial
                            )

                            // Move to start
                            moveVideoPlayToStart()
                        }
                    }
                    this@MainActivity.dialogTool.alertDialog?.dismiss()
                }
            })
        }

        // Open volume mix
        mainBinding.btnVolume.setOnClickListener {

            // Send data to prepare showing
            dialogMixVolume.showDialogMixVolume(listMedia, listMusic)
            dialogMixVolume.onConfirmClickMixSound = object : OnConfirmClickMixSound {
                override fun onConfirmClick(
                    listMedia: MutableList<MixSoundModel>
                ) {
                    // Mapping new volume
                    listMedia.forEach { itemMix ->
                        if (itemMix.type == MEDIA_TYPE_VIDEO) {
                            this@MainActivity.listMedia.forEach { media ->
                                if (itemMix.itemId == media.mediaId) {
                                    media.volume = itemMix.volume
                                }
                            }
                        } else if (itemMix.type == MEDIA_TYPE_MUSIC) {
                            this@MainActivity.listMusic.forEachIndexed { index, music ->
                                if (itemMix.itemId == music.id && index == itemMix.indexMusic) {
                                    music.volume = itemMix.volume
                                }
                            }
                        }
                    }

                    // Update to manager
                    this@MainActivity.managerPlayerControl.updateVolumeForMedia(
                        listMusic,
                        this@MainActivity.listMedia
                    )
                }
            }
        }
    }

    /**
     * Seer time duration view
     */
    private fun configEventSeerDuration() {
        mainBinding.viewTimeLine.onDurationTrackScrollListener =
            object : OnDurationTrackScrollListener {
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

        // Media choose
        mainBinding.viewTimeLine.onItemMediaChoose = object : OnItemMediaChoose {
            override fun onItemMediaChoose(index: Int) {
                indexMediaSelect = index
            }

            override fun onItemMediaCancel(index: Int) {
            }

            override fun onLayerChoose(index: Int) {
            }

            override fun onItemSpecialChoose(index: Int) {
                Timber.e("Index of special choose : $index")
                indexSpecialClick = index
            }

            override fun onMusicChoose(index: Int) {
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

        // Check duration current, if is start, we no need scroll to start
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
                makeViewShowMedia()
            }
        }
    }

    /**
     * Make view init to show media
     */
    private fun makeViewShowMedia() {
        this@MainActivity.listMedia.addAll(MyDataSingleton.listMediaPick)
        this@MainActivity.mainBinding.viewTimeLine.addMediaAndCreateItemView(this@MainActivity.listMedia)
        this@MainActivity.managerPlayerControl.addMediasToPlayerQueue(this@MainActivity.listMedia)

        MyDataSingleton.listMediaPick.clear()
    }

    /**
     * Make duration auto play
     */
    private fun initHandleDuration() {
        this@MainActivity.runnableDurationTimeLine = Runnable {
            this@MainActivity.mainBinding.viewTimeLine.updateScrollOfMainView()
            this@MainActivity.runnableDurationTimeLine?.let {
                if (this@MainActivity.isPlaying) {
                    handleTask.postDelayed(it, 10)
                } else handleTask.removeCallbacks(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        managerPlayerControl.onResume(isPlaying)
    }

    override fun onPause() {
        super.onPause()
        managerPlayerControl.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        managerPlayerControl.onDestroy()
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