package com.dongnh.mastereditvideo.utils.exts

import java.util.*

/**
 * Project : MasterEditVideo
 * Created by DongNH on 05/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */

fun Long.stringForTime(): String {
    val totalSeconds = this / 1000

    val miSecond = this % 1000
    val seconds = totalSeconds % 60
    val minutes = totalSeconds / 60 % 60
    val hours = totalSeconds / 3600

    val mFormatter = Formatter()
    return if (hours > 0) {
        mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
    } else {
        mFormatter.format("%02d:%02d:%03d", minutes, seconds, miSecond).toString()
    }
}