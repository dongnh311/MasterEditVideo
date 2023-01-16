package com.dongnh.masteredit.transition.draw

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractShaderTransition
import java.nio.FloatBuffer

/**
 * Project : MasterEditVideo
 * Created by DongNH on 16/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class TransitionMainVertexShader(context: Context) : AbstractShaderTransition() {
    val VERTEX_SHADE = "TransitionMainVertexShader.glsl"

    // attrs and uniforms
    val A_POSITION = "a_Position"
    val A_TEXTURE_COORDINATES = "a_TextureCoordinates"

    var position = 0
    var inputTextureCoordinateinates: Int = 0

    init {
        initShader(
            arrayOf(TRANSITION_FOLDER + VERTEX_SHADE),
            GLES20.GL_VERTEX_SHADER,
            context
        )
    }

    override fun initLocation(programId: Int) {
        position = GLES20.glGetAttribLocation(programId, A_POSITION)
        inputTextureCoordinateinates = GLES20.glGetAttribLocation(programId, A_TEXTURE_COORDINATES)
    }

    fun setposition(buffer: FloatBuffer) {
        GLES20.glEnableVertexAttribArray(position)
        buffer.position(0)
        GLES20.glVertexAttribPointer(
            position,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            buffer
        )
    }

    fun setinputTextureCoordinateinates(buffer: FloatBuffer) {
        GLES20.glEnableVertexAttribArray(inputTextureCoordinateinates)
        buffer.position(0)
        GLES20.glVertexAttribPointer(
            inputTextureCoordinateinates,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            buffer
        )
    }

    fun unsetposition() {
        GLES20.glDisableVertexAttribArray(position)
    }

    fun unsetinputTextureCoordinateinates() {
        GLES20.glDisableVertexAttribArray(inputTextureCoordinateinates)
    }
}