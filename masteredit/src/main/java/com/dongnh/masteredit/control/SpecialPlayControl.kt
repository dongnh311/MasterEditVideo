package com.dongnh.masteredit.control

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.*
import com.dongnh.masteredit.filter.GLBaseFilterObject
import com.dongnh.masteredit.filter.GLBrightnessFilterObject
import com.dongnh.masteredit.filter.GLContrastFilterObject
import com.dongnh.masteredit.filter.GLGammaFilterObject
import com.dongnh.masteredit.gl.GLFilterGroupObject
import com.dongnh.masteredit.model.SpecialModel
import com.dongnh.masteredit.utils.exts.getTransitionById
import com.dongnh.masteredit.view.GLPlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

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

    // List GL created
    private val listFilterAdded: MutableList<GLBaseFilterObject> = mutableListOf()

    // List Transition created
    private val listTransitionAdded: MutableList<AbstractTransition> = mutableListOf()

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
     * Config preview
     */
    fun configToPreview(glPlayerView: GLPlayerView) {
        this@SpecialPlayControl.glPlayerView = glPlayerView
        glPlayerView.setFilterRender(groupFilterDefault)
    }

    /**
     * Add special
     */
    fun addSpecialToHandlePreview(specialModel: SpecialModel) {
        groupFilterAdjusts.add(specialModel)
    }

    /**
     * Update transition to view
     */
    fun updateTransition(index: Int, specialModel: SpecialModel) {
        groupFilterAdjusts.forEachIndexed { indexItem, special ->
            if (indexItem == index && special.type == SPECIAL_TYPE_TRANSITION) {
                special.id = specialModel.id
                special.thumbnail = specialModel.thumbnail
                special.isAdded = false
                return@forEachIndexed
            }
        }

        // Remove on list create
        listFilterAdded.removeIf { glBase ->
            glBase.specialModel?.beginAt == specialModel.beginAt && glBase.specialModel?.endAt == specialModel.endAt
        }
    }

    fun removeFilter(indexSelect: Int) {
        var itemNeedRemove: SpecialModel? = null
        listFilterAdded.forEachIndexed { index, glFilter ->
            if (index == indexSelect && glFilter.specialModel != null) {
                itemNeedRemove = glFilter.specialModel
                return@forEachIndexed
            }
        }

        // Remove if need
        itemNeedRemove?.let { removeSpecial(it) }
    }

    /**
     * Remove special
     */
    fun removeSpecial(specialModel: SpecialModel) {
        val result = groupFilterAdjusts.remove(specialModel)
        if (!result) {
            var indexToRemove = -1
            groupFilterAdjusts.forEachIndexed { index, special ->
                if (specialModel.id == special.id && specialModel.beginAt == special.beginAt && specialModel.endAt == special.endAt) {
                    indexToRemove = index
                    return@forEachIndexed
                }
            }

            // Remove if find
            if (indexToRemove >= 0) {
                groupFilterAdjusts.removeAt(indexToRemove)
            }
        }

        var indexToRemove = -1
        listFilterAdded.forEachIndexed { index, glFilter ->
            if (glFilter.specialModel?.id == specialModel.id && glFilter.specialModel?.beginAt == specialModel.beginAt && glFilter.specialModel?.endAt == specialModel.endAt) {
                indexToRemove = index
                return@forEachIndexed
            }
        }

        // Remove if find
        if (indexToRemove >= 0) {
            listFilterAdded.removeAt(indexToRemove)
        }
    }

    /**
     * Clear all special added
     */
    fun releaseAllGLObjectAdded() {
        listFilterAdded.forEach {
            groupFilterDefault.filters.remove(it)
        }
        notifyTransitionChange(null)
        listFilterAdded.clear()
        listTransitionAdded.clear()
        groupFilterAdjusts.clear()

        groupFilterDefault = GLFilterGroupObject(
            mutableListOf(
                filterBrightness,
                filterContrast,
                filterGrammar
            )
        )

        // Make preview remove
        notifyPreviewCreateFilter()

        // Make transition is null
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

        // Add item to draw
        groupFilterAdjusts.forEach {
            when (it.type) {
                SPECIAL_TYPE_FILTER -> {
                    if (it.beginAt <= durationPlaying && it.endAt > durationPlaying) {
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
                    if (it.beginAt <= durationPlaying && it.endAt > durationPlaying) {
                        val progress =
                            1.0f - ((2000 - (durationPlaying - it.beginAt).toFloat() * 1.0f) / 2000)
                        // Add transition
                        handleTransition(
                            true,
                            it,
                            progress
                        )
                    } else {
                        // Remove filter
                        handleTransition(false, it, 0f)
                    }
                }
                SPECIAL_TYPE_GRAPH -> {

                }
                SPECIAL_TYPE_SPECIAL -> {

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
            if (!specialModel.isAdded) {
                specialModel.isAdded = true

                var glBaseFilterObject: GLBaseFilterObject? = null
                listFilterAdded.forEach { glFilter ->
                    if (glFilter.specialModel?.id == specialModel.id && glFilter.specialModel?.beginAt == specialModel.beginAt && glFilter.specialModel?.endAt == specialModel.endAt) {
                        glBaseFilterObject = glFilter
                        return@forEach
                    }
                }

                if (glBaseFilterObject == null) {
                    // Add new special
                    glBaseFilterObject =
                        GLBaseFilterObject(this@SpecialPlayControl.context, specialModel.lut)
                    glBaseFilterObject?.intensity = specialModel.intensity
                    glBaseFilterObject?.specialModel = specialModel

                    listFilterAdded.add(glBaseFilterObject!!)
                }

                // Update view
                groupFilterDefault.filters.add(glBaseFilterObject)
                notifyPreviewCreateFilter()
            }
        } else {
            if (specialModel.isAdded) {
                listFilterAdded.forEachIndexed { _, glFilter ->
                    if (glFilter.specialModel?.id == specialModel.id && glFilter.specialModel?.beginAt == specialModel.beginAt && glFilter.specialModel?.endAt == specialModel.endAt) {
                        groupFilterDefault.filters.remove(glFilter)
                        notifyPreviewCreateFilter()
                        Timber.e("Remove ok")
                        return@forEachIndexed
                    }
                }

                specialModel.isAdded = false
            }
        }
    }

    /**
     * Handle transition
     */
    private fun handleTransition(isAdd: Boolean, specialModel: SpecialModel, progress: Float) {
        if (isAdd) {
            if (!specialModel.isAdded && specialModel.id != ITEM_TRANSITION_NONE) {
                specialModel.isAdded = true

                var abstractTransition: AbstractTransition? = null
                listTransitionAdded.forEach { transition ->
                    if (transition.specialModel?.id == specialModel.id && transition.specialModel?.beginAt == specialModel.beginAt && transition.specialModel?.endAt == specialModel.endAt) {
                        abstractTransition = transition
                        return@forEach
                    }
                }

                if (abstractTransition == null) {
                    // Add new special
                    abstractTransition = getTransitionById(specialModel.id.toInt(), context)
                    abstractTransition!!.specialModel = specialModel
                    listTransitionAdded.add(abstractTransition!!)
                }

                // Update view
                abstractTransition!!.progress = progress
                notifyTransitionChange(abstractTransition)
            } else {
                // Update progress view
                listTransitionAdded.forEach { transition ->
                    if (transition.specialModel?.id == specialModel.id && transition.specialModel?.beginAt == specialModel.beginAt && transition.specialModel?.endAt == specialModel.endAt) {
                        transition.progress = progress
                        notifyTransitionChange(transition)
                        return@forEach
                    }
                }
            }
        } else {
            if (specialModel.isAdded) {
                listTransitionAdded.forEachIndexed { _, transition ->
                    if (transition.specialModel?.id == specialModel.id && transition.specialModel?.beginAt == specialModel.beginAt && transition.specialModel?.endAt == specialModel.endAt) {
                        // Update view
                        notifyTransitionChange(null)
                        transition.progress = 0f
                        Timber.e("Remove transition ok")
                        return@forEachIndexed
                    }
                }

                specialModel.isAdded = false
            }
        }
    }

    /**
     * Add filter to preview
     */
    private fun notifyPreviewCreateFilter() {
        CoroutineScope(Dispatchers.Default).launch {
            glPlayerView?.setFilterRender(groupFilterDefault)
            glPlayerView?.requestRender()
        }
    }

    /**
     * Update transition
     */
    private fun notifyTransitionChange(abstractTransition: AbstractTransition?) {
        CoroutineScope(Dispatchers.Default).launch {
            glPlayerView?.updateTransition(abstractTransition)
            glPlayerView?.requestRender()
        }
    }
}