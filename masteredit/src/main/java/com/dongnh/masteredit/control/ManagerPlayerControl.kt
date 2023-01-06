package com.dongnh.masteredit.control

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.dongnh.masteredit.model.MediaObject
import com.dongnh.masteredit.utils.interfaces.VideoEventLister
import java.util.*

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

    // List media
    private var listMediaAdded: MutableList<MediaObject> = mutableListOf()

    // Lister event
    var videoEventLister: VideoEventLister? = null

    /**
     * Add media to player
     */
    fun addMediasToPlayerQueue(listMediaObject: MutableList<MediaObject>) {
        releaseAllPlayer()
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

}