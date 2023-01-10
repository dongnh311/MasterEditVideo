package com.dongnh.masteredit.utils.exomanager

import com.dongnh.masteredit.utils.interfaces.MediaPlayEndListener
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.video.VideoSize

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class PlayerEventListener(private val exoPlayer: ExoPlayer) : Player.Listener {

    var mediaPlayEndListener: MediaPlayEndListener? = null

    override fun onPlayerError(error: PlaybackException) {
        if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
            exoPlayer.seekToDefaultPosition()
            exoPlayer.prepare()
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING -> {

            }
            ExoPlayer.STATE_ENDED -> {
                mediaPlayEndListener?.onEndPlay(exoPlayer.contentPosition, exoPlayer.currentPosition)
            }
            ExoPlayer.STATE_IDLE -> {}
            ExoPlayer.STATE_READY -> {}
            else -> {}
        }
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        mediaPlayEndListener?.onVideoSizeChange(videoSize)
    }
}