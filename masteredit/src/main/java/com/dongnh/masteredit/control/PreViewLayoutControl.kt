package com.dongnh.masteredit.control

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.dongnh.masteredit.const.VIEW_SIZE_16_9
import com.dongnh.masteredit.filter.GLBrightnessFilterObject
import com.dongnh.masteredit.filter.GLContrastFilterObject
import com.dongnh.masteredit.filter.GLGammaFilterObject
import com.dongnh.masteredit.gl.GLFilterObject
import com.dongnh.masteredit.gl.GLFilterGroupObject
import com.dongnh.masteredit.utils.interfaces.MediaPlayEndListener
import com.dongnh.masteredit.view.GLPlayerView
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Math.abs

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class PreViewLayoutControl(context: Context) {
    var glPlayerView: GLPlayerView = GLPlayerView(context, null)

    // Default filter
    private var filterBrightness = GLBrightnessFilterObject()
    private var filterContrast = GLContrastFilterObject()
    private var filterGrammar = GLGammaFilterObject()

    // List filter
    private val listFilterAdded = mutableListOf<GLFilterObject>()

    // Config for view
    var rotate = 0
    var ratioScreen: String = VIEW_SIZE_16_9
    var flipVertical: Boolean = false
    var flipHorizontal: Boolean = false

    /**
     * Init and prepare to view
     */
    fun initViewSizeToView() {
        val layoutParam = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParam.gravity = Gravity.CENTER
        glPlayerView.layoutParams = layoutParam

        // Init all default filter
        filterBrightness = GLBrightnessFilterObject()
        filterBrightness.brightness = 0.0f

        filterContrast = GLContrastFilterObject()
        filterContrast.contrast = 1.0f

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

        glPlayerView.post {
            glPlayerView.setRotate(
                kotlin.math.abs(rotate),
                ratioScreen,
                flipVertical,
                flipHorizontal
            )
        }

        glPlayerView.requestLayout()
        glPlayerView.requestRender()
        glPlayerView.onResume()
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
     * Pause player
     */
    fun onPause() {
        glPlayerView.onPause()
    }

    /**
     * On resume view
     */
    fun onResume() {
        glPlayerView.onResume()
    }

    /**
     * Release all
     */
    fun release() {
        glPlayerView.releaseAll()
    }

}