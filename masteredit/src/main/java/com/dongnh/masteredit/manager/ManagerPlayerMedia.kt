package com.dongnh.masteredit.manager

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.dongnh.masteredit.const.MEDIA_TYPE_VIDEO
import com.dongnh.masteredit.base.BasePlayerControl
import com.dongnh.masteredit.control.ImagePlayerControl
import com.dongnh.masteredit.control.PreViewLayoutControl
import com.dongnh.masteredit.control.VideoPlayerControl
import com.dongnh.masteredit.model.MediaObject
import com.dongnh.masteredit.utils.interfaces.MediaPlayEndListener
import com.dongnh.masteredit.utils.interfaces.VideoEventLister
import com.dongnh.masteredit.utils.interfaces.ViewChangeSizeListener
import com.google.android.exoplayer2.video.VideoSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class ManagerPlayerMedia(private val context: Context,
                         private val frameLayout: FrameLayout
) {

    // stack all view player
    private val stackViewPlayer: Stack<View> = Stack()

    // Duration in total played
    private var durationPlayed = 0L

    // Max duration of video project
    var durationOfVideoProject = 0L

    // Position duration
    private var currentDurationPlayer = 0L

    // List media
    private var listMediaAdded: MutableList<MediaObject> = mutableListOf()

    // Handle view
    private val preViewLayoutControl = PreViewLayoutControl(context)

    // View of media
    private var videoPlayerControl: VideoPlayerControl? = null

    // Lister event
    var videoEventLister: VideoEventLister? = null

    init {
        preViewLayoutControl.initViewSizeToView()
        // Add view to player
        frameLayout.addView(preViewLayoutControl.glPlayerView)
    }

    /**
     * Add media to player
     */
    fun addMediasToPlayerQueue(listMediaObject: MutableList<MediaObject>) {
        releaseAllPlayer()
        this@ManagerPlayerMedia.listMediaAdded.clear()
        this@ManagerPlayerMedia.listMediaAdded.addAll(listMediaObject)

        // Reset duration
        this@ManagerPlayerMedia.durationOfVideoProject = 0

        videoPlayerControl = VideoPlayerControl(this@ManagerPlayerMedia.context)

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
                this@ManagerPlayerMedia.currentDurationPlayer += adjDurationPlayed
                if ((adjDurationPlayed + currentDurationPlayer) == this@ManagerPlayerMedia.durationOfVideoProject) {
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
                preViewLayoutControl.glPlayerView.configSizeOfVideoToView(videoSize)
            }
        }

        // Set input source for view
        videoPlayerControl?.exoManager?.exoPlayer?.let {
            preViewLayoutControl.glPlayerView.setExoPlayer(
                it
            )
        }

        // Init media first
        videoPlayerControl?.initMediaPlayer(listMediaObject)

        // Calc duration of all media
        listMediaObject.forEachIndexed { _, mediaObject ->
            this@ManagerPlayerMedia.durationOfVideoProject += (mediaObject.endAt - mediaObject.beginAt)
        }
    }

    /**
     * Release all media and player created
     */
    fun releaseAllPlayer() {
        clearAllMediaPlayerCreated()
        clearAllViewAdded()
        this@ManagerPlayerMedia.listMediaAdded.clear()
    }

    /**
     * Clear all media player added
     */
    private fun clearAllMediaPlayerCreated() {
        videoPlayerControl?.releaseMedia()
    }

    /**
     * Clear view added to layout
     */
    private fun clearAllViewAdded() {
        this@ManagerPlayerMedia.stackViewPlayer.clear()
    }

    /**
     * Seek video to duration
     */
    fun seekVideoDuration(duration: Long) {
        // If start of video
        if (duration == 0L) {
            if (videoPlayerControl == null) {
                return
            }
            this@ManagerPlayerMedia.durationPlayed = 0L
            this@ManagerPlayerMedia.videoPlayerControl?.seekTo(0L)
        } else {
            if (videoPlayerControl == null) {
                return
            }
            this@ManagerPlayerMedia.durationPlayed = duration
            this@ManagerPlayerMedia.videoPlayerControl?.seekTo(duration)
        }
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
                this@ManagerPlayerMedia.videoPlayerControl?.playerMedia()
            } else {
                this@ManagerPlayerMedia.videoPlayerControl?.seekTo(this@ManagerPlayerMedia.durationPlayed)
                this@ManagerPlayerMedia.videoPlayerControl?.playerMedia()
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
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}