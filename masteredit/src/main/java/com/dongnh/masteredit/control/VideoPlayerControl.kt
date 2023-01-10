package com.dongnh.masteredit.control

import android.content.Context
import com.dongnh.masteredit.model.MediaObject
import com.dongnh.masteredit.utils.exomanager.ExoManager
import com.dongnh.masteredit.utils.interfaces.MediaPlayEndListener
import com.dongnh.masteredit.utils.interfaces.ViewChangeSizeListener
import com.google.android.exoplayer2.video.VideoSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive

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
    override val playbackProgressObservable = flow {
        repeat(100000) {
            if (currentCoroutineContext().isActive && isPlaying) {
                delay(200)
                val currentDurationPlayer = this@VideoPlayerControl.exoManager.exoPlayer.currentPosition - mediaObject.beginAt
                emit(currentDurationPlayer)
            }
        }
    }.flowOn(Dispatchers.Main)

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
                playEndListener?.onEndPlay(position, duration)
            }

            override fun onVideoSizeChange(videoSize: VideoSize) {
                viewChangeSizeListener?.onVideoSizeChange(videoSize)
            }
        }

        // Init media to play
        exoManager.createMediaItems(arrayListOf(mediaObject))
        exoManager.exoPlayer.prepare()
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