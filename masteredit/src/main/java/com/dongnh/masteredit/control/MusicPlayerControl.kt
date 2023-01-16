package com.dongnh.masteredit.control

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnErrorListener
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.dongnh.masteredit.model.MusicModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * Project : MasterEditVideo
 * Created by DongNH on 16/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class MusicPlayerControl(private val context: WeakReference<Context>) : MediaPlayer() {
    // Save object
    var musicModel: MusicModel? = null

    // Save index
    var indexOfMusic = -1

    // Flag check
    private var isPlayed = false

    // Duration player
    var currentDurationPlay = 0L

    // Handle task
    private var handle = Handler(Looper.getMainLooper())

    // Send data to view
    val playbackProgressObservable: Flow<Long> = flow {
        while (true) {
            if (isPlaying) {
                this@MusicPlayerControl.currentDurationPlay =
                    this@MusicPlayerControl.currentPosition.toLong() - musicModel?.beginAt!!
                // Call this thread again after 15 milliseconds => ~ 1000/60fps
                var maxDurationPlay = this@MusicPlayerControl.musicModel?.duration!!
                if (maxDurationPlay == 0L) {
                    maxDurationPlay = this@MusicPlayerControl.duration.toLong()
                }

                if (this@MusicPlayerControl.currentDurationPlay < maxDurationPlay) {
                    this@MusicPlayerControl.isPlayed = true
                } else {
                    Timber.e("Pause on update")
                    this@MusicPlayerControl.pause()
                    this@MusicPlayerControl.currentDurationPlay = 0
                }
            }
        }
    }

    /**
     * Init resource
     */
    fun initSourcePlay(musicModel: MusicModel) {
        this@MusicPlayerControl.musicModel = musicModel
        context.get()?.let { setDataSource(it, Uri.parse(musicModel.pathInLocal)) }
        @Suppress("DEPRECATION")
        setAudioStreamType(AudioManager.STREAM_MUSIC)

        setVolume(50f, 50f)

        prepare()

        // Lister error of this
        setOnErrorListener(onPlayErrorListener)
    }

    /**
     * Play music on start
     */
    fun playMusic() {
        if (!isPlayed) {
            isPlayed = true
        }

        // No played on before
        if (this@MusicPlayerControl.currentDurationPlay == 0L) {
            if (musicModel?.beginAt!! > 0L) {
                this@MusicPlayerControl.seekTo(musicModel?.beginAt!!.toInt())
            } else {
                this@MusicPlayerControl.seekTo(0)
            }
        } else {
            this@MusicPlayerControl.seekTo(this@MusicPlayerControl.currentDurationPlay.toInt())
        }

        this@MusicPlayerControl.start()
    }

    /**
     * Pause music
     */
    fun pauseMusic() {
        isPlayed = false
        this@MusicPlayerControl.pause()
        this@MusicPlayerControl.currentDurationPlay =
            this@MusicPlayerControl.currentPosition.toLong()
    }

    /**
     * Seek to duration and pause
     */
    fun seekMusicToDuration(duration: Long) {
        if (!isPlayed) {
            this@MusicPlayerControl.start()
            this@MusicPlayerControl.seekTo(0)
        }

        this@MusicPlayerControl.pause()
        var durationSeek = duration
        if (musicModel?.beginAt!! > 0L) {
            durationSeek += musicModel?.beginAt!!
        }
        this@MusicPlayerControl.seekTo(durationSeek.toInt())
        this@MusicPlayerControl.currentDurationPlay = durationSeek
        Timber.e("Seek music to : ${this@MusicPlayerControl.currentDurationPlay}")
    }

    /**
     * Release data and lister
     */
    fun releaseMusicPlayer() {
        this@MusicPlayerControl.handle.removeCallbacksAndMessages(null)
        this@MusicPlayerControl.musicModel = null
        this@MusicPlayerControl.release()
    }

    // Lister error callback
    private var onPlayErrorListener =
        OnErrorListener { _, what, extra ->
            Timber.e(String.format("OnErrorListener (%s, %s)", what, extra))
            if (what == MEDIA_ERROR_SERVER_DIED)
                musicModel?.let { initSourcePlay(it) }
            else if (what == MEDIA_ERROR_UNKNOWN)
                musicModel?.let { initSourcePlay(it) }
            true
        }

    /**
     * Check item music is added
     */
    fun checkMusicHaveAdded(index: Int, musicModel: MusicModel): Boolean {
        return index == indexOfMusic && musicModel == this@MusicPlayerControl.musicModel
    }
}