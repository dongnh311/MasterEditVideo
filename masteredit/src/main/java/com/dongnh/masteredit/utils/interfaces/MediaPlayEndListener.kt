package com.dongnh.masteredit.utils.interfaces

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
interface MediaPlayEndListener {
    fun onPreparePlay(position: Long, duration: Long)
    fun onEndPlay(position: Long, duration: Long)
}