package com.dongnh.masteredit.manager

import android.content.Context
import android.widget.FrameLayout
import com.dongnh.masteredit.const.ITEM_TRANSITION_NONE
import com.dongnh.masteredit.const.SPECIAL_TYPE_TRANSITION
import com.dongnh.masteredit.control.MusicPlayerControl
import com.dongnh.masteredit.control.PreViewLayoutControl
import com.dongnh.masteredit.control.SpecialPlayControl
import com.dongnh.masteredit.control.VideoPlayerControl
import com.dongnh.masteredit.model.MediaModel
import com.dongnh.masteredit.model.MusicModel
import com.dongnh.masteredit.model.SpecialModel
import com.dongnh.masteredit.utils.interfaces.MediaPlayEndListener
import com.dongnh.masteredit.utils.interfaces.VideoEventLister
import com.dongnh.masteredit.utils.interfaces.ViewChangeSizeListener
import com.google.android.exoplayer2.video.VideoSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class ManagerPlayerMedia(private val context: Context,
                         private val frameLayout: FrameLayout
) {

    // Duration in total played
    var durationPlayed = 0L

    // Max duration of video project
    var durationOfVideoProject = 0L

    // List media
    private var listMediaAdded: MutableList<MediaModel> = mutableListOf()

    // List Music
    private var lisMusicAdded: MutableList<MusicModel> = mutableListOf()

    // List transition
    private var listTransition: MutableList<SpecialModel> = mutableListOf()

    // Handle view
    private val preViewLayoutControl = PreViewLayoutControl(context)

    // View of media
    private var videoPlayerControl: VideoPlayerControl? = null

    // List music
    private val listMusicPlayer: MutableList<MusicPlayerControl> = mutableListOf()

    // Lister event
    var videoEventLister: VideoEventLister? = null

    // Handle filter, effect
    private val specialPlayControl by lazy {
        SpecialPlayControl(this@ManagerPlayerMedia.context)
    }

    init {
        preViewLayoutControl.initViewSizeToView()
        // Add view to player
        frameLayout.addView(preViewLayoutControl.preview())
        specialPlayControl.configToPreview(preViewLayoutControl.preview())
    }

    /**
     * Add media to player
     */
    fun addMediasToPlayerQueue(listMediaModel: MutableList<MediaModel>) {
        releaseAllPlayer()
        clearAllMusicAdded()
        this@ManagerPlayerMedia.specialPlayControl.releaseAllGLObjectAdded()
        this@ManagerPlayerMedia.listMediaAdded.clear()
        this@ManagerPlayerMedia.listMediaAdded.addAll(listMediaModel)

        // Reset duration
        this@ManagerPlayerMedia.durationOfVideoProject = 0

        // Init if need
        if (videoPlayerControl == null) {
            videoPlayerControl = VideoPlayerControl(this@ManagerPlayerMedia.context)
        }

        videoPlayerControl?.playEndListener = object : MediaPlayEndListener {
            override fun onPreparePlay(position: Long, duration: Long) {
                Timber.e("Prepare play index $position, with $duration")
            }

            override fun onEndPlay(position: Long, duration: Long) {
                Timber.e("Play end of index $position, with duration : $duration")
                if (position.toInt() == listMediaAdded.size - 1) {
                    videoPlayerControl?.pauseMedia()
                    videoEventLister?.onPlayOverEnd()
                }
            }

            override fun onVideoSizeChange(videoSize: VideoSize) {
                Timber.e("playEndListener onVideoSizeChange")
            }
        }

        // Lister data change
        CoroutineScope(Dispatchers.Default).launch {
            videoPlayerControl?.playbackProgressObservable?.catch { error ->
                Timber.e(error)
            }?.onEach { adjDurationPlayed ->
                videoEventLister?.onPlayWithProgress(adjDurationPlayed)
                this@ManagerPlayerMedia.durationPlayed += adjDurationPlayed

                // Make filter run on play
                this@ManagerPlayerMedia.specialPlayControl.playingVideo(this@ManagerPlayerMedia.durationPlayed)

                if ((adjDurationPlayed + durationPlayed) == this@ManagerPlayerMedia.durationOfVideoProject) {
                    Timber.e("Play end of all")
                    CoroutineScope(Dispatchers.Main).launch {
                        videoEventLister?.onPlayOverEnd()
                    }
                }
            }?.collect()
        }

        // Change size if need
        videoPlayerControl?.viewChangeSizeListener = object : ViewChangeSizeListener {
            override fun onVideoSizeChange(videoSize: VideoSize) {
                preViewLayoutControl.configSizeVideoForPreview(videoSize)
            }
        }

        // Set input source for view
        videoPlayerControl?.exoManager?.exoPlayer?.let {
            preViewLayoutControl.configExoPlayerToPreview(
                it
            )
        }

        // Init media first
        videoPlayerControl?.initMediaPlayer(listMediaModel)

        // Calc duration of all media
        listMediaModel.forEachIndexed { _, mediaObject ->
            this@ManagerPlayerMedia.durationOfVideoProject += (mediaObject.endAt - mediaObject.beginAt)
        }

        // Set max progress for special handle
        this@ManagerPlayerMedia.specialPlayControl.durationOfProject =
            this@ManagerPlayerMedia.durationOfVideoProject

        // Add music
        if (this@ManagerPlayerMedia.lisMusicAdded.isNotEmpty()) {
            val copyList = mutableListOf<MusicModel>()
            copyList.addAll(this@ManagerPlayerMedia.lisMusicAdded)
            addMusicToQueue(copyList)
        }

        // Create transition
        createOfEditTransition()
    }

    /**
     * Add music to player music control
     */
    fun addMusicToQueue(listMusicModel: MutableList<MusicModel>) {
        lisMusicAdded.clear()
        lisMusicAdded.addAll(listMusicModel)

        if (this@ManagerPlayerMedia.listMusicPlayer.isEmpty()) {
            listMusicModel.forEachIndexed { index, music ->
                addMusicToList(index, music)
            }
        } else {
            listMusicModel.forEachIndexed { index, musicModel ->
                var isAdded = false
                this@ManagerPlayerMedia.listMusicPlayer.forEach { musicPlayer ->
                    if (musicPlayer.checkMusicHaveAdded(index, musicModel)) {
                        isAdded = true
                        return@forEach
                    }
                }
                if (!isAdded) {
                    addMusicToList(index, musicModel)
                }
            }
        }
    }

    /**
     * Handle transition create by media
     */
    private fun createOfEditTransition() {
        if (this@ManagerPlayerMedia.listTransition.size != this@ManagerPlayerMedia.listMediaAdded.size - 1) {
            // Adjust if edit
            if (this@ManagerPlayerMedia.listTransition.isNotEmpty()) {
                var durationOfMedia = 0L
                // Remove item is not correct
                this@ManagerPlayerMedia.listMediaAdded.forEachIndexed { index, mediaModel ->
                    var isAdded = false
                    durationOfMedia += mediaModel.endAt - mediaModel.beginAt
                    this@ManagerPlayerMedia.listTransition.forEachIndexed { indexSpec, special ->
                        if (indexSpec == index) {
                            // Replace if not mapping
                            if (special.itemIdBefore != mediaModel.mediaId || special.itemIdNext != this@ManagerPlayerMedia.listMediaAdded[index + 1].mediaId) {
                                val specialModel = SpecialModel()
                                specialModel.id = ITEM_TRANSITION_NONE
                                specialModel.type = SPECIAL_TYPE_TRANSITION
                                specialModel.indexBefore = index
                                specialModel.indexNext = index + 1
                                specialModel.itemIdBefore = mediaModel.mediaId
                                specialModel.itemIdNext =
                                    this@ManagerPlayerMedia.listMediaAdded[index + 1].mediaId
                                specialModel.beginAt = durationOfMedia - 1000L
                                specialModel.endAt = durationOfMedia + 1000L

                                this@ManagerPlayerMedia.listTransition[indexSpec] = specialModel
                            }
                            isAdded = true
                        }
                    }
                    if (!isAdded) {
                        if (index < this@ManagerPlayerMedia.listMediaAdded.size - 1) {
                            val specialModel = SpecialModel()
                            specialModel.id = ITEM_TRANSITION_NONE
                            specialModel.type = SPECIAL_TYPE_TRANSITION
                            specialModel.indexBefore = index
                            specialModel.indexNext = index + 1
                            specialModel.itemIdBefore = mediaModel.mediaId
                            specialModel.itemIdNext =
                                this@ManagerPlayerMedia.listMediaAdded[index + 1].mediaId
                            specialModel.beginAt = durationOfMedia - 1000L
                            specialModel.endAt = durationOfMedia + 1000L

                            this@ManagerPlayerMedia.listTransition.add(specialModel)
                        }
                    }
                }
            } else {
                // Add new
                var durationOfMedia = 0L
                this@ManagerPlayerMedia.listMediaAdded.forEachIndexed { index, mediaModel ->
                    durationOfMedia += mediaModel.endAt - mediaModel.beginAt
                    if (index < this@ManagerPlayerMedia.listMediaAdded.size - 1) {
                        val specialModel = SpecialModel()
                        specialModel.id = 600004 // TODO : For test
                        specialModel.type = SPECIAL_TYPE_TRANSITION
                        specialModel.indexBefore = index
                        specialModel.indexNext = index + 1
                        specialModel.itemIdBefore = mediaModel.mediaId
                        specialModel.itemIdNext =
                            this@ManagerPlayerMedia.listMediaAdded[index + 1].mediaId
                        specialModel.beginAt = durationOfMedia - 1000L
                        specialModel.endAt = durationOfMedia + 1000L

                        this@ManagerPlayerMedia.listTransition.add(specialModel)
                    }
                }
            }
        }

        // Update new item to special player
        this@ManagerPlayerMedia.listTransition.forEach {
            specialPlayControl.addSpecialToHandlePreview(it)
        }
    }

    /**
     * Create music play and add to list
     */
    private fun addMusicToList(index: Int, musicModel: MusicModel) {
        val musicPlayerControl = MusicPlayerControl(WeakReference(context))
        musicPlayerControl.indexOfMusic = index
        musicPlayerControl.initSourcePlay(musicModel)
        this@ManagerPlayerMedia.listMusicPlayer.add(musicPlayerControl)
    }

    /**
     * Release all media and player created
     */
    fun releaseAllPlayer() {
        clearAllMusicAdded()
        this@ManagerPlayerMedia.listMediaAdded.clear()
        videoPlayerControl?.releaseMedia()
        preViewLayoutControl.release()
    }

    /**
     * Clear music added to manager
     */
    private fun clearAllMusicAdded() {
        this@ManagerPlayerMedia.listMusicPlayer.forEach { musicPlayerControl ->
            musicPlayerControl.releaseMusicPlayer()
        }

        this@ManagerPlayerMedia.listMusicPlayer.clear()
    }

    /**
     * Seek video to duration
     */
    fun seekVideoDuration(duration: Long) {
        if (videoPlayerControl == null) {
            return
        }
        this@ManagerPlayerMedia.durationPlayed = duration
        this@ManagerPlayerMedia.videoPlayerControl?.seekTo(duration)
        this@ManagerPlayerMedia.listMusicPlayer.forEach {
            it.seekMusicToDuration(duration)
        }
        this@ManagerPlayerMedia.specialPlayControl.playingVideo(duration)
    }

    /**
     * Start play video
     */
    fun playVideo() {
        try {
            if (videoPlayerControl == null) {
                return
            }

            // If play from start
            if (this@ManagerPlayerMedia.durationPlayed == 0L) {
                this@ManagerPlayerMedia.videoPlayerControl?.seekTo(0L)

                // Seek music
                this@ManagerPlayerMedia.listMusicPlayer.forEach {
                    it.seekMusicToDuration(0L)
                }
            } else {
                this@ManagerPlayerMedia.videoPlayerControl?.seekTo(this@ManagerPlayerMedia.durationPlayed)
                // Seek music
                this@ManagerPlayerMedia.listMusicPlayer.forEach {
                    it.seekMusicToDuration(this@ManagerPlayerMedia.durationPlayed)
                }
            }

            // Play media
            this@ManagerPlayerMedia.videoPlayerControl?.playerMedia()

            // Play music
            this@ManagerPlayerMedia.listMusicPlayer.forEach {
                it.playMusic()
            }

        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    /**
     * Pause All play
     */
    fun pauseAllPlay() {
        try {
            if (videoPlayerControl == null) {
                return
            }
            videoPlayerControl?.pauseMedia()

            // Pause music
            this@ManagerPlayerMedia.listMusicPlayer.forEach {
                it.pauseMusic()
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    /**
     * Resume view and media
     */
    fun onResume(isPlaying : Boolean) {
        preViewLayoutControl.onResume()
        if (isPlaying) {
            videoPlayerControl?.playerMedia()

            this@ManagerPlayerMedia.listMusicPlayer.forEach {
                it.playMusic()
            }
        }
    }

    /**
     * Stop if need
     */
    fun onPause() {
        preViewLayoutControl.onPause()
        videoPlayerControl?.pauseMedia()
        // Pause music
        this@ManagerPlayerMedia.listMusicPlayer.forEach {
            it.pauseMusic()
        }
    }

    /**
     * Update volume for player
     */
    fun updateVolumeForMedia(
        listMusicModel: MutableList<MusicModel>,
        listMediaModel: MutableList<MediaModel>
    ) {
        // Update to music
        listMusicModel.forEachIndexed { index, musicModel ->
            this@ManagerPlayerMedia.listMusicPlayer.forEach { musicPlayerControl ->
                if (musicPlayerControl.musicModel?.id == musicModel.id && index == musicPlayerControl.indexOfMusic) {
                    musicPlayerControl.musicModel?.volume = musicModel.volume
                    musicPlayerControl.setVolume(musicModel.volume, musicModel.volume)
                }
            }

            this@ManagerPlayerMedia.lisMusicAdded.forEachIndexed { indexCurrent, musicModelCurrent ->
                if (musicModelCurrent.id == musicModel.id && index == indexCurrent) {
                    musicModelCurrent.volume = musicModel.volume
                }
            }
        }

        // Update to video
        listMediaModel.forEach { media ->
            this@ManagerPlayerMedia.listMediaAdded.forEach { mediaModel ->
                if (media.mediaId == mediaModel.mediaId) {
                    mediaModel.volume = media.volume
                }
            }
        }

        // Update volume for video player
        this@ManagerPlayerMedia.videoPlayerControl?.updateVolumeForMedia(listMediaModel)
    }

    /**
     * Add special to preview
     */
    fun addSpecialToPreview(specialModel: SpecialModel) {
        this@ManagerPlayerMedia.specialPlayControl.addSpecialToHandlePreview(specialModel)
    }

    /**
     * Remove special
     */
    fun removeSpecialFromPreview(specialModel: SpecialModel) {
        this@ManagerPlayerMedia.specialPlayControl.removeSpecial(specialModel)
    }

    /**
     * Clear all media player, preview
     */
    fun onDestroy() {
        releaseAllPlayer()
    }
}