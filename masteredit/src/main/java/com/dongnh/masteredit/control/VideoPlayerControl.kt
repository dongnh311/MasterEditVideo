package com.dongnh.masteredit.control

import android.content.Context
import com.dongnh.masteredit.base.BasePlayerControl
import com.dongnh.masteredit.const.MEDIA_TYPE_IMAGE
import com.dongnh.masteredit.const.MEDIA_TYPE_VIDEO
import com.dongnh.masteredit.model.MediaObject
import com.dongnh.masteredit.utils.exomanager.ExoManager
import com.dongnh.masteredit.utils.interfaces.MediaPlayEndListener
import com.dongnh.masteredit.utils.interfaces.ViewChangeSizeListener
import com.google.android.exoplayer2.video.VideoSize
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import kotlin.math.abs

/**
 * Project : MasterEditVideo
 * Created by DongNH on 09/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class VideoPlayerControl(context: Context) {

    // Exo player
    val exoManager: ExoManager = ExoManager(context)

    // Save current player
    private var isPlaying = false

    // Save object
    var mediaObjects = mutableListOf<MediaObject>()

    // Total duration of media added
    var totalDurationOfMediaAdded = 0L

    // Duration play
    var currentDurationPlayer = 0L

    // Video size
    var viewChangeSizeListener: ViewChangeSizeListener? = null

    // Send data to view
    val playbackProgressObservable : Flow<Long> = flow {
        while (true) {
            if (isPlaying) {
                // Find index of media first
                val indexOfMedia = withContext(Dispatchers.Main) {
                    return@withContext this@VideoPlayerControl.exoManager.exoPlayer.currentMediaItemIndex
                }

                // Calc adj duration player
                var exoPlayerDuration = withContext(Dispatchers.Main) {
                    return@withContext this@VideoPlayerControl.exoManager.exoPlayer.currentPosition
                }

                if (indexOfMedia > mediaObjects.size - 1) {
                    return@flow
                }

                // Make it > 0
                if (exoPlayerDuration < 0) {
                    exoPlayerDuration = 0
                }

                val duration = if (indexOfMedia == 0) 0 else findDurationForIndexMedia(indexOfMedia)

                // Check type of media
                var adjDuration = exoPlayerDuration + duration - currentDurationPlayer
                if (adjDuration < 0) {
                    adjDuration = exoPlayerDuration
                }

                emit(adjDuration)
                currentDurationPlayer += adjDuration
                delay(200)
            }
        }
    }

    // lister when play to end
    var playEndListener: MediaPlayEndListener? = null

    /**
     * Init media
     */
    fun initMediaPlayer(mediaObjects: MutableList<MediaObject>) {
        this@VideoPlayerControl.mediaObjects.clear()
        this@VideoPlayerControl.mediaObjects.addAll(mediaObjects)

        // Call back to view
        exoManager.mediaPlayEndListener = object : MediaPlayEndListener {
            override fun onPreparePlay(position: Long, duration: Long) {
                playEndListener?.onPreparePlay(position, duration)
            }

            override fun onEndPlay(position: Long, duration: Long) {
                if (this@VideoPlayerControl.exoManager.exoPlayer.currentMediaItemIndex > this@VideoPlayerControl.mediaObjects.size - 1) {
                    return
                }
                playEndListener?.onEndPlay(this@VideoPlayerControl.exoManager.exoPlayer.currentMediaItemIndex.toLong(), duration)
            }

            override fun onVideoSizeChange(videoSize: VideoSize) {
                viewChangeSizeListener?.onVideoSizeChange(videoSize)
            }
        }

        // Init media to play
        exoManager.createMediaItems(this@VideoPlayerControl.mediaObjects)
        exoManager.exoPlayer.prepare()

        // Make it not play
        exoManager.exoPlayer.playWhenReady = false

        // Calc duration
        calcTotalDuration()
    }

    /**
     * Start play video
     */
    fun playerMedia() {
        if (!isPlaying) {
            isPlaying = true
            exoManager.exoPlayer.playWhenReady = true
        } else {
            exoManager.exoPlayer.playWhenReady = false
            pauseMedia()
        }
    }

    /**
     * Pause video
     */
    fun pauseMedia() {
        isPlaying = false
        exoManager.exoPlayer.playWhenReady = false
    }

    /**
     * Seek to duration
     */
    fun seekTo(currentPosition: Long) {
        pauseMedia()

        // Make item start is running
        if (currentPosition == 0L) {
            exoManager.exoPlayer.seekTo(0, 0L)
            this@VideoPlayerControl.currentDurationPlayer = 0
        } else {
            var durationOfClip = 0L
            for (index in 0 until mediaObjects.size) {
                val mediaMainObject = mediaObjects[index]
                durationOfClip += mediaMainObject.mediaDuration
                var durationNeed = 0

                if (durationOfClip > currentPosition) {
                    val durationSeek = durationOfClip - currentPosition
                    durationNeed = (mediaMainObject.mediaDuration - durationSeek - 100).toInt()
                    val currentDurationPlayer =
                        durationNeed + mediaMainObject.beginAt
                    // Seek to next
                    exoManager.exoPlayer.seekTo(index, currentDurationPlayer)
                    break
                }
            }
            this@VideoPlayerControl.currentDurationPlayer = currentPosition +  durationOfClip
        }
    }

    /**
     * Release
     */
    fun releaseMedia() {
        isPlaying = false
        mediaObjects.clear()
        exoManager.exoPlayer.release()
        playbackProgressObservable.cancellable()
    }

    /**
     * Calc for total duration
     */
    private fun calcTotalDuration() {
        this@VideoPlayerControl.totalDurationOfMediaAdded = 0L
        mediaObjects.forEachIndexed { _, mediaObject ->
            this@VideoPlayerControl.totalDurationOfMediaAdded += mediaObject.endAt - mediaObject.beginAt
        }
    }

    /**
     * Get duration of media before index
     */
    private fun findDurationForIndexMedia(indexOfMedia: Int): Long {
        var durationNeed = 0L
        mediaObjects.forEachIndexed { index, mediaObject ->
            if (index < indexOfMedia) {
                durationNeed += mediaObject.endAt - mediaObject.beginAt
            } else {
                return@forEachIndexed
            }
        }

        return durationNeed
    }
}