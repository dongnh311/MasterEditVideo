package com.dongnh.masteredit.control

import android.content.Context
import com.dongnh.masteredit.model.MediaObject
import com.dongnh.masteredit.utils.exomanager.ExoManager
import com.dongnh.masteredit.utils.interfaces.MediaPlayEndListener
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

    // Duration play
    var currentDurationPlayer = 0L

    // Send data to view
    override val playbackProgressObservable = flow {
        repeat(100000) {
            if (currentCoroutineContext().isActive && exoManager.exoPlayer.playWhenReady) {
                delay(200)
                currentDurationPlayer = this@VideoPlayerControl.exoManager.exoPlayer.currentPosition
                emit(currentDurationPlayer)
            }
        }
    }.flowOn(Dispatchers.Main)

    // lister when play to end
    var playEndListener: MediaPlayEndListener? = null

    /**
     * Init media
     */
    override fun initMediaPlayer(index: Int, mediaObject: MediaObject) {
        this@VideoPlayerControl.mediaObject = mediaObject
        this@VideoPlayerControl.indexOfMedia = index

        // Call back to view
        exoManager.mediaPlayEndListener = object : MediaPlayEndListener {
            override fun onPreparePlay(position: Long, duration: Long) {
                playEndListener?.onPreparePlay(position, duration)
            }

            override fun onEndPlay(position: Long, duration: Long) {
                playEndListener?.onEndPlay(position, duration)
            }
        }

        // Init media to play
        exoManager.createMediaItems(arrayListOf(mediaObject))
    }

    /**
     * Start play video
     */
    override fun playerMedia() {
        super.playerMedia()
        if (exoManager.exoPlayer.playWhenReady) {
            pauseMedia()
        } else {
            exoManager.exoPlayer.playWhenReady = true
        }

    }

    /**
     * Pause video
     */
    override fun pauseMedia() {
        super.pauseMedia()
        exoManager.exoPlayer.playWhenReady = false
    }
}