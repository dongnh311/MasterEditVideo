package com.dongnh.masteredit.utils.exomanager

import android.content.Context
import android.net.Uri
import com.dongnh.masteredit.const.MEDIA_TYPE_IMAGE
import com.dongnh.masteredit.const.MEDIA_TYPE_TRANSITION
import com.dongnh.masteredit.const.MEDIA_TYPE_VIDEO
import com.dongnh.masteredit.model.MediaObject
import com.dongnh.masteredit.utils.interfaces.MediaPlayEndListener
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class ExoManager(context: Context) {

    var exoPlayer: ExoPlayer

    private var startAutoPlay = true

    private var startWindow = 0
    private var startPosition: Long = 0

    private val mediaItems: MutableList<MediaItem> = mutableListOf()
    private val listMedia : MutableList<MediaObject> = mutableListOf()

    var mediaPlayEndListener : MediaPlayEndListener? = null

    init {
        exoPlayer = ExoPlayer.Builder(context)
            .setRenderersFactory(DefaultRenderersFactory(context))
            .build()

        exoPlayer.setAudioAttributes(AudioAttributes.DEFAULT, true)
        val playerEventListener = PlayerEventListener(exoPlayer)
        exoPlayer.addListener(PlayerEventListener(exoPlayer))

        // Send to view
        playerEventListener.mediaPlayEndListener = object : MediaPlayEndListener {
            override fun onEndPlay(position: Long, duration: Long) {
                mediaPlayEndListener?.onEndPlay(position, duration)
            }

            override fun onPreparePlay(position: Long, duration: Long) {
                mediaPlayEndListener?.onPreparePlay(position, duration)
                // Seer to start
                exoPlayer.seekTo(position.toInt(), listMedia[position.toInt()].beginAt)
                exoPlayer.prepare()
            }
        }

        exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
        exoPlayer.playWhenReady = startAutoPlay

        val haveStartPosition = startWindow != C.INDEX_UNSET
        if (haveStartPosition) {
            exoPlayer.seekTo(startWindow, startPosition)
        }

        // Create empty first
        exoPlayer.setMediaItems(mediaItems, !haveStartPosition)
        exoPlayer.prepare()
    }

    /**
     * Create media for play
     */
    fun createMediaItems(listMediaItem: MutableList<MediaObject>) {
        // Clear all first
        mediaItems.clear()
        listMedia.clear()
        listMedia.addAll(listMediaItem)

        // Add to list play
        listMediaItem.forEach {
            val url: Uri = when (it.mediaType) {
                MEDIA_TYPE_VIDEO -> {
                    Uri.parse(it.mediaPath)
                }
                MEDIA_TYPE_IMAGE -> {
                    Uri.parse(it.mediaPath)
                }
                MEDIA_TYPE_TRANSITION -> {
                    Uri.parse(it.mediaPath)
                }
                else -> {
                    Uri.EMPTY
                }
            }

            val builder: MediaItem.Builder = MediaItem.Builder()
                .setUri(url)
                .setMimeType("mime_type")
                .setSubtitleConfigurations(emptyList())
            mediaItems.add(builder.build())
        }

        exoPlayer.setMediaItems(mediaItems, false)
        exoPlayer.prepare()
    }

    /**
     * Update position
     */
    private fun updateStartPosition() {
        startAutoPlay = exoPlayer.playWhenReady
        startWindow = exoPlayer.currentMediaItemIndex
        startPosition = 0.coerceAtLeast(exoPlayer.contentPosition.toInt()).toLong()
    }

    /**
     * Move to next media
     */
    fun moveToNextMedia() {
        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNextMediaItem()
        }
    }

    /**
     * Release Player
     */
    private fun releasePlayer() {
        updateStartPosition()
        exoPlayer.release()
        mediaItems.clear()
    }
}