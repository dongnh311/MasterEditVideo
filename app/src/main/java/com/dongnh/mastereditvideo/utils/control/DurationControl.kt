package com.dongnh.mastereditvideo.utils.control

import android.widget.TextView
import com.dongnh.mastereditvideo.utils.exts.stringForTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Project : MasterEditVideo
 * Created by DongNH on 05/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class DurationControl(private val viewDuration: TextView) {
    var currentDuration = 0L
    var adjDuration = 0L
    var totalDuration = 0L

    /**
     * Set duration for video play
     */
    fun setDuration(durationFormat: Long) {
        this@DurationControl.adjDuration = durationFormat
        this@DurationControl.currentDuration += durationFormat
        CoroutineScope(Dispatchers.Main).launch {
            this@DurationControl.viewDuration.text = currentDuration.stringForTime()
        }
    }

    /**
     * Set duration for seek on scroll
     */
    fun setDurationSeek(durationFormat: Long) {
        this@DurationControl.currentDuration = durationFormat
        CoroutineScope(Dispatchers.Main).launch {
            this@DurationControl.viewDuration.text = currentDuration.stringForTime()
        }
    }

    /**
     * Reset value
     */
    fun resetValueOfDuration() {
        currentDuration = 0L
        adjDuration = 0L
        totalDuration = 0L
        CoroutineScope(Dispatchers.Main).launch {
            this@DurationControl.viewDuration.text = currentDuration.stringForTime()
        }
    }
}