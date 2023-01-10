package com.dongnh.masteredit.base

import com.dongnh.masteredit.model.MediaObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import java.util.concurrent.Flow

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

    /**
     * Init media
     */
    abstract fun initMediaPlayer(indexOfMedia: Int, mediaObject: MediaObject)

    /**
     * Play media
     */
    abstract fun playerMedia()

    /**
     * Pause media
     */
    abstract fun pauseMedia()

    /**
     * Seek media to duration
     */
    abstract fun seekTo(currentPosition: Long)

    /**
     * Release media
     */
    abstract fun releaseMedia()
}