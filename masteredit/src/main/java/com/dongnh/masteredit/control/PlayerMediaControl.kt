package com.dongnh.masteredit.control

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import com.dongnh.masteredit.const.VIEW_SIZE_16_9
import com.dongnh.masteredit.filter.GLBrightnessFilterObject
import com.dongnh.masteredit.filter.GLContrastFilterObject
import com.dongnh.masteredit.filter.GLGammaFilterObject
import com.dongnh.masteredit.gl.GLFilterObject
import com.dongnh.masteredit.gl.GLFilterGroupObject
import com.dongnh.masteredit.utils.exomanager.ExoManager
import com.dongnh.masteredit.utils.interfaces.MediaPlayEndListener
import com.dongnh.masteredit.view.GLPlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.lang.Math.abs

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class PlayerMediaControl(context: Context) {
    val glPlayerView: GLPlayerView = GLPlayerView(context, null)
    val exoManager: ExoManager = ExoManager(context)

    // Default filter
    private var filterBrightness = GLBrightnessFilterObject()
    private var filterContrast = GLContrastFilterObject()
    private var filterGrammar = GLGammaFilterObject()

    // List filter
    private val listFilterAdded = mutableListOf<GLFilterObject>()

    // lister when play to end
    var playEndListener: MediaPlayEndListener? = null

    // Config for view
    var rotate = 0
    var ratioScreen: String = VIEW_SIZE_16_9
    var flipVertical: Boolean = false
    var flipHorizontal: Boolean = false

    // Send data to view
    val playbackProgressObservable = flow {
        delay(200)
        emit(this@PlayerMediaControl.exoManager.exoPlayer.currentPosition)
    }

    /**
     * Init and prepare to view
     */
    fun initViewAndPrepareData() {
        // Call back to view
        exoManager.mediaPlayEndListener = object : MediaPlayEndListener {
            override fun onPreparePlay(position: Long, duration: Long) {
                playEndListener?.onPreparePlay(position, duration)
            }

            override fun onEndPlay(position: Long, duration: Long) {
                playEndListener?.onEndPlay(position, duration)
            }
        }

        glPlayerView.setExoPlayer(exoManager.exoPlayer)

        val layoutParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParam.gravity = Gravity.CENTER
        glPlayerView.layoutParams = layoutParam

        // Init all default filter
        filterBrightness = GLBrightnessFilterObject()
        filterBrightness.brightness = 0.5f

        filterContrast = GLContrastFilterObject()
        filterContrast.contrast = 0.5f

        filterGrammar = GLGammaFilterObject()
        filterGrammar.gamma = 0.5f

        // Using group for add all
        val glFilterGroup = GLFilterGroupObject(
            listOf(
                filterBrightness,
                filterContrast,
                filterGrammar
            )
        )
        glPlayerView.addFilterRender(glFilterGroup)
        glPlayerView.onResume()

        glPlayerView.post {
            glPlayerView.setRotate(
                abs(rotate),
                ratioScreen,
                flipVertical,
                flipHorizontal
            )
        }
    }

    /**
     * Add filter
     */
    fun configNewFilter(glFilterObject: GLFilterObject) {
        listFilterAdded.add(glFilterObject)

        val filterGroup = GLFilterGroupObject(
            listOf(
                filterBrightness,
                filterContrast,
                filterGrammar,
                glFilterObject
            )
        )
        glPlayerView.addFilterRender(filterGroup)
    }

    /**
     * Start play video
     */
    fun playVideo() {
        if (exoManager.exoPlayer.playWhenReady) {
            pauseVideo()
        } else {
            exoManager.exoPlayer.playWhenReady = true
        }
    }

    /**
     * Pause player
     */
    fun onPause() {
        glPlayerView.onPause()
    }

    fun pauseVideo() {
        exoManager.exoPlayer.playWhenReady = false
    }
}