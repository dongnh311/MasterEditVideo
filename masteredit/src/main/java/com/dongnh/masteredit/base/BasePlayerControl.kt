package com.dongnh.masteredit.base

import com.dongnh.masteredit.model.MediaModel

/**
 * Project : MasterEditVideo
 * Created by DongNH on 09/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
abstract class BasePlayerControl {

    // Save object
    var mediaModels = mutableListOf<MediaModel>()

    // Save current index
    var indexOfMedia = -1

    /**
     * Init media
     */
    abstract fun initMediaPlayer(indexOfMedia: Int, mediaModels: MutableList<MediaModel>)

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