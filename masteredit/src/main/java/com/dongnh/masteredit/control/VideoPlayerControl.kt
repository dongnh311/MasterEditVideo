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
class VideoPlayerControl(context: Context): BasePlayerControl() {

    val exoManager: ExoManager = ExoManager(context)

    // Save current player
    private var isPlaying = false

    // Duration play
    var currentDurationPlayer = 0L

    // Video size
    var viewChangeSizeListener: ViewChangeSizeListener? = null

    // Send data to view
    val playbackProgressObservable : Flow<Long> = flow {
        while (true) {
            if (isPlaying) {
                val exoPlayerDuration = withContext(Dispatchers.Main) {
                    return@withContext this@VideoPlayerControl.exoManager.exoPlayer.currentPosition
                }
                val currentDurationPlayer = exoPlayerDuration - mediaObject.beginAt
                emit(currentDurationPlayer)

                delay(200)
            }
        }
    }



    // lister when play to end
    var playEndListener: MediaPlayEndListener? = null

    /**
     * Init media
     */
    override fun initMediaPlayer(indexOfMedia: Int, mediaObject: MediaObject) {
        this@VideoPlayerControl.mediaObject = mediaObject
        this@VideoPlayerControl.indexOfMedia = indexOfMedia

        // Call back to view
        exoManager.mediaPlayEndListener = object : MediaPlayEndListener {
            override fun onPreparePlay(position: Long, duration: Long) {
                playEndListener?.onPreparePlay(position, duration)
            }

            override fun onEndPlay(position: Long, duration: Long) {
                playEndListener?.onEndPlay(this@VideoPlayerControl.indexOfMedia.toLong(), duration)
            }

            override fun onVideoSizeChange(videoSize: VideoSize) {
                viewChangeSizeListener?.onVideoSizeChange(videoSize)
            }
        }

        // Init media to play
        exoManager.createMediaItems(arrayListOf(mediaObject))
        exoManager.exoPlayer.prepare()

        // Make it not play
        exoManager.exoPlayer.playWhenReady = false
    }

    /**
     * Start play video
     */
    override fun playerMedia() {
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
    override fun pauseMedia() {
        isPlaying = false
        exoManager.exoPlayer.playWhenReady = false
    }

    /**
     * Seek to duration
     */
    override fun seekTo(currentPosition: Long) {
        this@VideoPlayerControl.currentDurationPlayer = currentPosition + mediaObject.beginAt
        exoManager.exoPlayer.seekTo(this@VideoPlayerControl.currentDurationPlayer)
    }

    /**
     * Release
     */
    override fun releaseMedia() {
        exoManager.exoPlayer.release()
    }
}