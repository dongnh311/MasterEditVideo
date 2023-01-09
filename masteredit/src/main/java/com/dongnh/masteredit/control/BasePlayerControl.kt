package com.dongnh.masteredit.control

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
abstract class BasePlayerControl {

    // Save object
    var mediaObject = MediaObject()

    // Save current index
    var indexOfMedia = -1

    // Send data to view
    open val playbackProgressObservable = flow<Long> {}.flowOn(Dispatchers.Main)

    /**
     * Init media
     */
    open fun initMediaPlayer(indexOfMedia: Int, mediaObject: MediaObject) {}

    /**
     * Play media
     */
    open fun playerMedia() {}

    /**
     * Pause media
     */
    open fun pauseMedia() {}

    /**
     * Seek media to duration
     */
    open fun seekTo(currentPosition: Long) {}
}