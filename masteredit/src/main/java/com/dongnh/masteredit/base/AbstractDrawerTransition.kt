package com.dongnh.masteredit.base

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader
import com.dongnh.masteredit.transition.draw.TransitionMainVertexShader
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Project : MasterEditVideo
 * Created by DongNH on 16/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
abstract class AbstractDrawerTransition(private var context: Context) {

    companion object {
        const val SIZEOF_FLOAT = 4
    }

    var programId = 0

    var vertexPositionBuffer: FloatBuffer? = null
    var inputCoordinateBuffer: FloatBuffer? = null

    var vertexShader: TransitionMainVertexShader? = null
    var transitionShader: TransitionMainFragmentShader? = null

    init {
        vertexShader = TransitionMainVertexShader(context)
        getTransitionShader()
        transitionShader?.getShaderId()?.let { loadProgram(vertexShader!!.getShaderId(), it) }
        vertexShader?.initLocation(programId)
        transitionShader?.initLocation(programId)
        inputCoordinateBuffer = createFloatBuffer(
            floatArrayOf(
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
            )
        )
        vertexPositionBuffer = createFloatBuffer(
            floatArrayOf(
                -1.0f, 1.0f,
                -1.0f, -1.0f,
                1.0f, 1.0f,
                1.0f, -1.0f
            )
        )
    }

    fun setProgress(progress: Float) {
        GLES20.glUseProgram(programId)
        transitionShader?.setUProgress(progress)
    }

    fun setRatio(ratio: Float) {
        GLES20.glUseProgram(programId)
        transitionShader?.setURatio(ratio)
    }

    fun setToRatio(ratio: Float) {
        GLES20.glUseProgram(programId)
        transitionShader?.setUToRatio(ratio)
    }

    fun draw(
        textureId: Int,
        textureId2: Int,
        posX: Int,
        posY: Int,
        width: Int,
        height: Int
    ) {
        GLES20.glUseProgram(programId)
        GLES20.glViewport(posX, posY, width, height)
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        vertexPositionBuffer?.let { vertexShader?.setposition(it) }
        inputCoordinateBuffer?.let {
            @Suppress("LeakingThis")
            vertexShader?.setinputTextureCoordinateinates(it)
        }
        transitionShader?.setUInputTexture(GLES20.GL_TEXTURE0, textureId)
        transitionShader?.setUInputTexture2(GLES20.GL_TEXTURE3, textureId2)

        // draw filter
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        vertexShader?.unsetposition()
        vertexShader?.unsetinputTextureCoordinateinates()
    }

    open fun loadProgram(vararg shaderIds: Int) {
        val result = IntArray(1)
        val programId = GLES20.glCreateProgram()
        for (id in shaderIds) {
            GLES20.glAttachShader(programId, id)
        }
        GLES20.glLinkProgram(programId)
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, result, 0)
        if (result[0] <= 0) {
            Timber.e("Load Program Linking Failed")
            return
        }
        for (id in shaderIds) {
            GLES20.glDeleteShader(id)
        }
        this.programId = programId
    }

    fun createFloatBuffer(coords: FloatArray): FloatBuffer? {
        // Allocate a direct ByteBuffer, using 4 bytes per float, and copy coords into it.
        val bb = ByteBuffer.allocateDirect(coords.size * SIZEOF_FLOAT)
        bb.order(ByteOrder.nativeOrder())
        val fb = bb.asFloatBuffer()
        fb.put(coords)
        fb.position(0)
        return fb
    }

    fun release() {
        if (programId != -1) {
            GLES20.glDeleteProgram(programId)
        }

        vertexShader = null
        transitionShader = null
    }

    protected abstract fun getTransitionShader()
}