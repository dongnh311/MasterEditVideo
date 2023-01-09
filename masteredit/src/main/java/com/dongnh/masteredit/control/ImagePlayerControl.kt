package com.dongnh.masteredit.control

import android.content.Context
import com.dongnh.masteredit.model.MediaObject
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
class ImagePlayerControl(private val context: Context) : BasePlayerControl() {

    // Max duration view image
    var maxDuration = 2000L

    // Duration of play
    private var countDuration = 0L

    // Status playing
    var isPlaying = false

    override val playbackProgressObservable = flow {
        repeat(100000) {
            if (currentCoroutineContext().isActive && isPlaying) {
                delay(50)
                countDuration += 50
                emit(countDuration)
            }
        }
    }.flowOn(Dispatchers.Main)

    override fun initMediaPlayer(indexOfMedia: Int, mediaObject: MediaObject) {
        super.initMediaPlayer(indexOfMedia, mediaObject)
        this.mediaObject = mediaObject
        this.indexOfMedia = indexOfMedia
    }

    override fun playerMedia() {
        super.playerMedia()
        isPlaying = true
    }

    override fun pauseMedia() {
        super.pauseMedia()
        isPlaying = false
    }

    override fun seekTo(currentPosition: Long) {
        super.seekTo(currentPosition)
        isPlaying = false
        countDuration = currentPosition
    }
}