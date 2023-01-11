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

    // List player
    private val listControlPlayer = mutableListOf<BasePlayerControl>()

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

        listMediaObject.forEachIndexed { index, mediaObject ->
            val playerControl: BasePlayerControl
            if (mediaObject.mediaType == MEDIA_TYPE_VIDEO) {
                playerControl = VideoPlayerControl(this@ManagerPlayerMedia.context)
                playerControl.playEndListener = object : MediaPlayEndListener {
                    override fun onPreparePlay(position: Long, duration: Long) {
                        Timber.e("Prepare play index $position, with $duration")
                    }

                    override fun onEndPlay(position: Long, duration: Long) {
                        Timber.e("Play end of index $position, with $duration")
                        playerControl.pauseMedia()
                    }

                    override fun onVideoSizeChange(videoSize: VideoSize) {
                        Timber.e("playEndListener onVideoSizeChange")
                    }
                }

                // Lister data change
                CoroutineScope(Dispatchers.Default).launch {
                    val castItem = playerControl as VideoPlayerControl
                    castItem.playbackProgressObservable.catch { error ->
                        Timber.e(error)
                    }.onEach { durationPlayed ->
                        if (durationPlayed != this@ManagerPlayerMedia.currentDurationPlayer
                            && durationPlayed < castItem.mediaObject.mediaDuration
                        ) {
                            if (this@ManagerPlayerMedia.currentDurationPlayer > durationPlayed) {
                                this@ManagerPlayerMedia.currentDurationPlayer = 0
                            }
                            val adj = durationPlayed - this@ManagerPlayerMedia.currentDurationPlayer
                            this@ManagerPlayerMedia.currentDurationPlayer = durationPlayed
                            this@ManagerPlayerMedia.durationPlayed += adj
                            //Timber.e("Video played duration : ${this@ManagerPlayerMedia.durationPlayed}")
                            videoEventLister?.onPlayWithProgress(adj)
                        } else if (durationPlayed + castItem.mediaObject.beginAt >= castItem.mediaObject.mediaDuration) {
                            Timber.e("Index : ${castItem.indexOfMedia}, Begin at : ${castItem.mediaObject.beginAt}, current : ${durationPlayed}, duration video : ${castItem.mediaObject.mediaDuration}")

                            // Stop listener
                            CoroutineScope(Dispatchers.Main).launch {
                                castItem.pauseMedia()
                                findAndShowItemPlay(castItem.indexOfMedia + 1, isPlay = true)
                            }
                        }
                    }.collect()
                }

                // Prepare to play
                if (index == 0) {
                    // Change size if need
                    playerControl.viewChangeSizeListener = object : ViewChangeSizeListener {
                        override fun onVideoSizeChange(videoSize: VideoSize) {
                            preViewLayoutControl.glPlayerView.configSizeOfVideoToView(videoSize)
                        }
                    }
                    preViewLayoutControl.glPlayerView.setExoPlayer(playerControl.exoManager.exoPlayer)
                }

                // Init media first
                playerControl.initMediaPlayer(index, mediaObject)
            } else {
                playerControl = ImagePlayerControl(this@ManagerPlayerMedia.context)
                playerControl.initMediaPlayer(index, mediaObject)
            }

            // Calc duration of all media
            this@ManagerPlayerMedia.durationOfVideoProject += mediaObject.mediaDuration

            this@ManagerPlayerMedia.listControlPlayer.add(playerControl)
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
        this@ManagerPlayerMedia.stackViewPlayer.forEach {

        }
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
            if (this@ManagerPlayerMedia.listControlPlayer.isEmpty()) {
                return
            }
            this@ManagerPlayerMedia.listControlPlayer[0].seekTo(0L)
        } else {
            this@ManagerPlayerMedia.durationPlayed = duration
            var durationOfClip = 0L

            for (index in 0 until listMediaAdded.size) {
                val mediaMainObject = listMediaAdded[index]
                durationOfClip += mediaMainObject.mediaDuration
                var durationNeed = 0

                if (durationOfClip > duration) {
                    val durationSeek = durationOfClip - duration
                    durationNeed = (mediaMainObject.mediaDuration - durationSeek - 100).toInt()
                    this@ManagerPlayerMedia.currentDurationPlayer =
                        durationNeed + mediaMainObject.startTime

                    findAndShowItemOnSeek(index, durationNeed.toLong())
                    break
                }

                findAndShowItemPlay(durationNeed, true)
            }
        }
    }

    /**
     * Start play video
     */
    fun playVideo() {
        try {
            if (listControlPlayer.isEmpty()) {
                return
            }

            // If play from start
            if (this@ManagerPlayerMedia.durationPlayed == 0L) {
                this@ManagerPlayerMedia.listControlPlayer[0].seekTo(0L)
                this@ManagerPlayerMedia.listControlPlayer[0].playerMedia()
            } else {
                // Resume video
                var durationOfClip = 0L
                var isPlay = false
                for (index in 0 until listMediaAdded.size) {
                    val mediaMainObject = listMediaAdded[index]
                    durationOfClip += mediaMainObject.mediaDuration
                    if (durationOfClip > this@ManagerPlayerMedia.durationPlayed) {
                        isPlay = true
                        findAndShowItemPlay(index, true)
                        break
                    }

                    // Check if play on last duration
                    // We need move player to start
                    if (!isPlay) {
                        Timber.e("We need to go to start time of video")
                        this@ManagerPlayerMedia.pauseAllPlay()
                    }
                }
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
            if (listControlPlayer.isEmpty()) {
                return
            }
            listControlPlayer.forEach {
                it.pauseMedia()
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    /**
     * Find view and seek to duration
     */
    private fun findAndShowItemOnSeek(indexOfVideo: Int, duration: Long) {
        try {
            when (val view = listControlPlayer[indexOfVideo]) {
                is VideoPlayerControl -> {
                    // Switch to preview
                    preViewLayoutControl.glPlayerView.setExoPlayer(view.exoManager.exoPlayer)
                    view.seekTo(duration)
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    /**
     * Find view and seek to duration
     */
    private fun findAndShowItemPlay(indexOfVideo: Int, isPlay: Boolean) {
        try {
            val view = listControlPlayer[indexOfVideo]
            if (view is VideoPlayerControl) {
                if (isPlay) {
                    // Switch to preview
                    preViewLayoutControl.glPlayerView.setExoPlayer(view.exoManager.exoPlayer)
                    view.playerMedia()
                } else {
                    view.pauseMedia()
                }
            } else if (view is ImagePlayerControl) {
                if (isPlay) {
                    view.playerMedia()
                } else {
                    view.pauseMedia()
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

}