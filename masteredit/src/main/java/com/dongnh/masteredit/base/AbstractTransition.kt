package com.dongnh.masteredit.base

import android.opengl.GLES20
import com.dongnh.masteredit.model.SpecialModel
import timber.log.Timber

/**
 * Project : MasterEditVideo
 * Created by DongNH on 16/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
abstract class AbstractTransition(
    protected var name: String?,
    protected var type: Int
) {

    var progress = 0f
    var drawer: AbstractDrawerTransition? = null

    // Save object
    var specialModel: SpecialModel? = null
    var index = -1

    protected abstract fun getDrawer()

    protected open fun setDrawerParams() {}

    private var renderLeft = 0
    private var renderTop: Int = 0
    private var renderRight: Int = 0
    private var renderBottom: Int = 0

    private var textureInput = -1
    private var textureOutput = -1

    private var outputWidth = 0
    private var outputHeight = 0

    private val offScreenTextureIds = IntArray(2)

    fun initTextureOutput(width: Int, height: Int) {
        this@AbstractTransition.outputWidth = width
        this@AbstractTransition.outputHeight = height
        GLES20.glGenTextures(offScreenTextureIds.size, offScreenTextureIds, 0)
        for (mTextureId in offScreenTextureIds) {
            // bind to fbo texture cause we are going to do setting.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId)
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height,
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null
            )
            // Set the minification filter to use the color of the pixel with the closest coordinates in the texture as the pixel color to be drawn
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST.toFloat()
            )
            // Set the magnification filter to use the colors with the closest coordinates in the texture, and use the weighted average algorithm to obtain the pixel color to be drawn
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR.toFloat()
            )
            // Set the wrapping direction S, and intercept the texture coordinates to [1/2n,1-1/2n]. will result in never merging with the border
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            // Set the wrapping direction T, intercept the texture coordinates to [1/2n,1-1/2n]. will result in never merging with the border
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            // unbind fbo texture.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        }
    }

    fun initDrawSize(top: Int, left: Int, bottom: Int, right: Int) {
        renderLeft = left
        renderTop = top
        renderBottom = bottom
        renderRight = right
    }

    fun initTextureId(textureInput: Int) {
        this@AbstractTransition.textureInput = textureInput
    }

    open fun exec() {
        if (textureInput != -1) {
            if (drawer == null) {
                getDrawer()
            }
            drawer?.setProgress(progress)
            Timber.e("Progress of transition : $progress")
            setDrawerParams()

            drawer?.draw(
                offScreenTextureIds[1],
                textureInput,
                renderLeft,
                renderBottom,
                renderRight,
                renderTop
            )
        } else {
            Timber.e("Input screen for transition is null")
        }
    }

    open fun checkRational(): Boolean {
        return true
    }

    override fun toString(): String {
        return "AbstractTransition{" +
                "mName='" + name + '\'' +
                ", mType=" + type +
                '}'
    }

    open fun release() {
        drawer?.release()
    }
}