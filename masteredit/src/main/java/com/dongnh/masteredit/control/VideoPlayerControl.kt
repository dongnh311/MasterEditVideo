package com.dongnh.masteredit.control

import android.content.Context
import com.dongnh.masteredit.base.BasePlayerControl
import com.dongnh.masteredit.model.MediaObject
import com.dongnh.masteredit.utils.exomanager.ExoManager
import com.dongnh.masteredit.utils.interfaces.MediaPlayEndListener
import com.dongnh.masteredit.utils.interfaces.ViewChangeSizeListener
import com.google.android.exoplayer2.video.VideoSize
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

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

    // Save current index is view
    var indexOfMediaOnView = -1

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
                var exoPlayerDuration = withContext(Dispatchers.Main) {
                    return@withContext this@VideoPlayerControl.exoManager.exoPlayer.currentPosition
                }
                if (exoPlayerDuration < 0) {
                    exoPlayerDuration = 0
                }

                val indexOfMedia = withContext(Dispatchers.Main) {
                    return@withContext this@VideoPlayerControl.exoManager.exoPlayer.currentMediaItemIndex
                }

                val duration = findDurationForIndexMedia(indexOfMedia)
                val adjDuration = exoPlayerDuration + duration - currentDurationPlayer

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
        this@VideoPlayerControl.mediaObjects = mediaObjects

        // Call back to view
        exoManager.mediaPlayEndListener = object : MediaPlayEndListener {
            override fun onPreparePlay(position: Long, duration: Long) {
                playEndListener?.onPreparePlay(position, duration)
            }

            override fun onEndPlay(position: Long, duration: Long) {
                playEndListener?.onEndPlay(this@VideoPlayerControl.exoManager.exoPlayer.currentMediaItemIndex.toLong(), duration)
            }

            override fun onVideoSizeChange(videoSize: VideoSize) {
                viewChangeSizeListener?.onVideoSizeChange(videoSize)
            }
        }

        // Init media to play
        exoManager.createMediaItems(mediaObjects)
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
        exoManager.exoPlayer.release()
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
            if (index <= indexOfMedia) {
                durationNeed += mediaObject.endAt - mediaObject.beginAt
            } else {
                return@forEachIndexed
            }
        }

        return durationNeed
    }
}