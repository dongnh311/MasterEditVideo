package com.dongnh.masteredit.control

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.dongnh.masteredit.model.MediaObject
import com.dongnh.masteredit.utils.interfaces.MediaPlayEndListener
import com.dongnh.masteredit.utils.interfaces.VideoEventLister
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class ManagerPlayerControl(private val context: Context,
    private val frameLayout: FrameLayout) {

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

    // Handle view and player
    private val playerMediaControl = PlayerMediaControl(context)

    // Lister event
    var videoEventLister: VideoEventLister? = null

    init {
        // Add view to player
        frameLayout.addView(playerMediaControl.glPlayerView)
        playerMediaControl.playEndListener = object : MediaPlayEndListener {
            override fun onPreparePlay(position: Long, duration: Long) {
                Timber.e("Prepare play index $position, with $duration")
            }

            override fun onEndPlay(position: Long, duration: Long) {
                Timber.e("Play end of index $position, with $duration")
            }
        }

        playerMediaControl.initViewAndPrepareData()
        CoroutineScope(Dispatchers.Default).launch {
            playerMediaControl.playbackProgressObservable.catch {
                Timber.e(it)
            }.onEach {
                Timber.e("Duration play : $it")
            }.collect()
        }
    }

    /**
     * Add media to player
     */
    fun addMediasToPlayerQueue(listMediaObject: MutableList<MediaObject>) {
        releaseAllPlayer()
        this@ManagerPlayerControl.listMediaAdded.clear()
        this@ManagerPlayerControl.listMediaAdded.addAll(listMediaObject)
        playerMediaControl.exoManager.createMediaItems(listMediaObject)
    }

    /**
     * Release all media and player created
     */
    fun releaseAllPlayer() {
        clearAllMediaPlayerCreated()
        clearAllViewAdded()
        this@ManagerPlayerControl.listMediaAdded.clear()
    }

    /**
     * Clear all media player added
     */
    private fun clearAllMediaPlayerCreated() {
        this@ManagerPlayerControl.stackViewPlayer.forEach {

        }
    }

    /**
     * Clear view added to layout
     */
    private fun clearAllViewAdded() {
        this@ManagerPlayerControl.frameLayout.removeAllViews()
        this@ManagerPlayerControl.frameLayout.invalidate()
        this@ManagerPlayerControl.stackViewPlayer.clear()
    }

    /**
     * Seek video to duration
     */
    fun seekVideoDuration(duration: Long) {
        // If start of video
        if (duration == 0L) {
            playerMediaControl.exoManager.exoPlayer.seekTo(0, 0)
        } else {
            this@ManagerPlayerControl.durationPlayed = duration
            var durationOfClip = 0L

            for (index in 0 until listMediaAdded.size) {
                val mediaMainObject = listMediaAdded[index]
                durationOfClip += mediaMainObject.mediaDuration

                if (durationOfClip > duration) {
                    val durationSeek = durationOfClip - duration
                    val durationNeed = mediaMainObject.mediaDuration - durationSeek - 100
                    this@ManagerPlayerControl.currentDurationPlayer =
                        durationNeed + mediaMainObject.startTime
                    break
                }
            }

            playerMediaControl.exoManager.exoPlayer.seekTo(0, durationOfClip)
        }
    }

    /**
     * Start play video
     */
    fun playVideo() {
        try {
            playerMediaControl.playVideo()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    /**
     * Pause All play
     */
    fun pauseAllPlay() {
        try {
            playerMediaControl.pauseVideo()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

}