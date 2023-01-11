package com.dongnh.masteredit.control

import android.content.Context
import com.dongnh.masteredit.base.BasePlayerControl
import com.dongnh.masteredit.model.MediaObject
import com.dongnh.masteredit.utils.exomanager.ExoManager
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

    // Using exo to view image
    val exoManager: ExoManager = ExoManager(context)

    // Max duration view image
    var maxDuration = 2000L

    // Duration of play
    private var countDuration = 0L

    // Status playing
    var isPlaying = false

    val playbackProgressObservable = flow {
        repeat(100000) {
            if (currentCoroutineContext().isActive && isPlaying) {
                delay(50)
                countDuration += 50
                emit(countDuration)
            }
        }
    }.flowOn(Dispatchers.Main)

    override fun initMediaPlayer(indexOfMedia: Int, mediaObjects: MutableList<MediaObject>) {
        this.mediaObjects = mediaObjects
        this.indexOfMedia = indexOfMedia

        // Init media to play
        exoManager.createMediaItems(mediaObjects)
    }

    override fun playerMedia() {
        isPlaying = true
    }

    override fun pauseMedia() {
        isPlaying = false
    }

    override fun seekTo(currentPosition: Long) {
        isPlaying = false
        countDuration = currentPosition
    }

    override fun releaseMedia() {

    }
}