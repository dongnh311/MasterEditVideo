package com.dongnh.masteredit.control

import android.content.Context
import com.dongnh.masteredit.const.*
import com.dongnh.masteredit.filter.GLBaseFilterObject
import com.dongnh.masteredit.filter.GLBrightnessFilterObject
import com.dongnh.masteredit.filter.GLContrastFilterObject
import com.dongnh.masteredit.filter.GLGammaFilterObject
import com.dongnh.masteredit.gl.GLFilterGroupObject
import com.dongnh.masteredit.model.SpecialModel
import com.dongnh.masteredit.view.GLPlayerView

/**
 * Project : MasterEditVideo
 * Created by DongNH on 17/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class SpecialPlayControl(private val context: Context) {
    // Preview
    private var glPlayerView: GLPlayerView? = null

    // Total duration of project
    var durationOfProject = 0L

    // Duration is playing
    var durationIsPlaying = 0L

    // Make filter default
    private var groupFilterDefault: GLFilterGroupObject

    // Make adj filters
    private val groupFilterAdjusts: MutableList<SpecialModel> = mutableListOf()

    // Init all default filter
    private val filterBrightness = GLBrightnessFilterObject()
    private val filterContrast = GLContrastFilterObject()
    private val filterGrammar = GLGammaFilterObject()

    init {
        filterBrightness.brightness = 0.0f
        filterContrast.contrast = 1.0f
        filterGrammar.gamma = 0.5f

        groupFilterDefault = GLFilterGroupObject(
            mutableListOf(
                filterBrightness,
                filterContrast,
                filterGrammar
            )
        )
    }

    /**
     * Add special
     */
    fun addSpecialToHandlePreview(specialModel: SpecialModel) {
        groupFilterAdjusts.add(specialModel)
    }

    /**
     * Remove special
     */
    fun removeSpecial(specialModel: SpecialModel) {
        val result = groupFilterAdjusts.remove(specialModel)
        if (!result) {
            var indexToRemove = -1
            groupFilterAdjusts.forEachIndexed { index, special ->
                if (specialModel.id == special.id) {
                    indexToRemove = index
                    return@forEachIndexed
                }
            }

            // Remove if find
            if (indexToRemove >= 0) {
                groupFilterAdjusts.removeAt(indexToRemove)
            }
        }
    }

    /**
     * Make filter show when play video
     */
    fun playingVideo(durationPlaying: Long) {
        this@SpecialPlayControl.durationIsPlaying = durationPlaying

        // Add filter to view follow duration
        if (this@SpecialPlayControl.durationIsPlaying >= durationOfProject) {
            return
        }

        groupFilterAdjusts.forEach {
            when (it.type) {
                SPECIAL_TYPE_FILTER -> {
                    if (it.beginAt >= durationPlaying && it.endAt < durationPlaying) {
                        // Add filter
                        handleFilter(true, it)

                    } else {
                        // Remove filter
                        handleFilter(false, it)
                    }
                }
                SPECIAL_TYPE_EFFECT -> {

                }
                SPECIAL_TYPE_TRANSITION -> {

                }
                SPECIAL_TYPE_GRAPH -> {

                }
                SPECIAL_TYPE_NONE -> {

                }
            }

        }
    }

    /**
     * Add or remove filter
     */
    private fun handleFilter(isAdd: Boolean, specialModel: SpecialModel) {
        if (isAdd) {
            val glBaseFilterObject =
                GLBaseFilterObject(this@SpecialPlayControl.context, specialModel.lut)
            glBaseFilterObject.intensity = specialModel.intensity
            groupFilterDefault.filters.add(glBaseFilterObject)
            glPlayerView?.addFilterRender(groupFilterDefault)
        } else {
            groupFilterDefault.filters.removeIf { it is GLBaseFilterObject }
        }
    }
}