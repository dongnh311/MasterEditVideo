package com.dongnh.masteredit.utils.exomanager

import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback
import com.dongnh.masteredit.const.MEDIA_TYPE_IMAGE
import com.dongnh.masteredit.const.MEDIA_TYPE_TRANSITION
import com.dongnh.masteredit.const.MEDIA_TYPE_VIDEO
import com.dongnh.masteredit.enums.FormatVideoOut
import com.dongnh.masteredit.enums.NAME_EXIF_IMAGE_VIDEO
import com.dongnh.masteredit.model.MediaObject
import com.dongnh.masteredit.utils.exts.createMediaTransformPath
import com.dongnh.masteredit.utils.interfaces.MediaPlayEndListener
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.video.VideoSize
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.File

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class ExoManager(private val context: Context) {

    // Exo
    var exoPlayer: ExoPlayer

    // Make auto play
    private var startAutoPlay = true

    // Index of expo
    private var startWindow = 0
    private var startPosition: Long = 0

    // List media added to exo
    private val mediaItems: MutableList<MediaItem> = mutableListOf()

    // List media raw
    private val listMedia : MutableList<MediaObject> = mutableListOf()

    // Call back to parent
    var mediaPlayEndListener : MediaPlayEndListener? = null

    // Resolution
    var resolution : FormatVideoOut = FormatVideoOut.RESOLUTION_HD

    init {
        exoPlayer = ExoPlayer.Builder(context)
            .setRenderersFactory(DefaultRenderersFactory(context))
            .build()

        exoPlayer.setAudioAttributes(AudioAttributes.DEFAULT, true)
        val playerEventListener = PlayerEventListener(exoPlayer)
        exoPlayer.addListener(playerEventListener)
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

            override fun onVideoSizeChange(videoSize: VideoSize) {
                Timber.e("onVideoSizeChange")
                mediaPlayEndListener?.onVideoSizeChange(videoSize)
            }
        }

        exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
        exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
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
        // Init media to play
        CoroutineScope(Dispatchers.Main).launch {
            // Clear all first
            mediaItems.clear()
            listMedia.clear()
            listMedia.addAll(listMediaItem)

            // Add to list play
            listMediaItem.forEach {
                var mimeTypes = MimeTypes.VIDEO_MP4
                val url: Uri = when (it.mediaType) {
                    MEDIA_TYPE_VIDEO -> {
                        mimeTypes = MimeTypes.VIDEO_MP4
                        Uri.parse(it.mediaPath)
                    }
                    MEDIA_TYPE_IMAGE -> {
                        val path = convertImageToVideo(it).shareIn(
                            scope = this,
                            replay = 1,
                            started = SharingStarted.WhileSubscribed()
                        )
                        val result = path.first()

                        mimeTypes = MimeTypes.VIDEO_MP4
                        Timber.e("pathString = $result")
                        Uri.parse(result)
                    }
                    MEDIA_TYPE_TRANSITION -> {
                        mimeTypes = MimeTypes.VIDEO_MP4
                        Uri.parse(it.mediaPath)
                    }
                    else -> {
                        Uri.EMPTY
                    }
                }

                val builder: MediaItem.Builder = MediaItem.Builder()
                    .setUri(url)
                    .setMimeType(mimeTypes)
                    .setSubtitleConfigurations(emptyList())
                mediaItems.add(builder.build())

            }

            exoPlayer.setMediaItems(mediaItems, false)
            exoPlayer.prepare()
        }
    }

    /**
     * Convert image to video
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun convertImageToVideo(mediaObject: MediaObject) = callbackFlow {
        val fileInput = File(mediaObject.mediaPath)
        val info = ExifInterface(mediaObject.mediaPath)

        val withImage = info.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0)
        val heightImage = info.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0)

        var withToNewVideo = 1280
        var heightToNewVideo = 720
        when (resolution) {
            FormatVideoOut.RESOLUTION_HD -> {
                if (withImage < heightImage) {
                    withToNewVideo = 720
                    heightToNewVideo = 1280
                }
            }
            FormatVideoOut.RESOLUTION_FULL_HD -> {
                withToNewVideo = 1920
                heightToNewVideo = 1080
                if (withImage < heightImage) {
                    withToNewVideo = 1080
                    heightToNewVideo = 1920
                }
            }
            FormatVideoOut.RESOLUTION_Q_HD -> {
                withToNewVideo = 2560
                heightToNewVideo = 1440
                if (withImage < heightImage) {
                    withToNewVideo = 1440
                    heightToNewVideo = 2560
                }
            }
        }

        val pathOfFile = createMediaTransformPath(context = context) + "/" + mediaObject.mediaName + NAME_EXIF_IMAGE_VIDEO
        val commandToRun = "-loop 1 -framerate 30 -i ${fileInput.path} -s $withToNewVideo:$heightToNewVideo -t 2 $pathOfFile -y"

        Timber.e("Command to run : $commandToRun")
        val ffmpegSessionCompleteCallback = FFmpegSessionCompleteCallback {
            mediaObject.pathVideoTransform = pathOfFile
            trySend(pathOfFile)
            Timber.e("Transform done")
        }
        FFmpegKit.executeAsync(commandToRun, ffmpegSessionCompleteCallback)
        awaitClose {}
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