package com.dongnh.masteredit.gl

import android.opengl.GLES20

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class GLPreviewFilterObject(texTarget: Int) : GLFilterObject() {

    val GL_TEXTURE_EXTERNAL_OES = 0x8D65

    val VERTEX_SHADER = """
        uniform mat4 uMVPMatrix;
        uniform mat4 uSTMatrix;
        uniform float uCRatio;
        attribute vec4 position;
        attribute vec4 inputTextureCoordinate;
        varying highp vec2 textureCoordinate;
        void main() {
        vec4 scaledPos = position;
        scaledPos.x = scaledPos.x * uCRatio;
        gl_Position = uMVPMatrix * scaledPos;
        textureCoordinate = (uSTMatrix * inputTextureCoordinate).xy;
        }
        
        """.trimIndent()

    var texTarget = 0

    init {
        val fragmentShader = createFragmentShaderSourceOESIfNeed(texTarget)
        this@GLPreviewFilterObject.vertexShaderSource = VERTEX_SHADER
        this@GLPreviewFilterObject.fragmentShaderSource = fragmentShader
        this@GLPreviewFilterObject.texTarget = texTarget
    }

    open fun createFragmentShaderSourceOESIfNeed(texTarget: Int): String {
        return if (texTarget == GL_TEXTURE_EXTERNAL_OES) {
            StringBuilder()
                .append("#extension GL_OES_EGL_image_external : require\n")
                .append(DEFAULT_FRAGMENT_SHADER.replace("sampler2D", "samplerExternalOES"))
                .toString()
        } else DEFAULT_FRAGMENT_SHADER
    }

    fun draw(texName: Int, mvpMatrix: FloatArray?, stMatrix: FloatArray?, aspectRatio: Float) {
        useProgram()
        GLES20.glUniformMatrix4fv(getHandle("uMVPMatrix"), 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(getHandle("uSTMatrix"), 1, false, stMatrix, 0)
        GLES20.glUniform1f(getHandle("uCRatio"), aspectRatio)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, getVertexBufferName())
        GLES20.glEnableVertexAttribArray(getHandle("position"))
        GLES20.glVertexAttribPointer(
            getHandle("position"),
            VERTICES_DATA_POS_SIZE,
            GLES20.GL_FLOAT,
            false,
            VERTICES_DATA_STRIDE_BYTES,
            VERTICES_DATA_POS_OFFSET
        )
        GLES20.glEnableVertexAttribArray(getHandle("inputTextureCoordinate"))
        GLES20.glVertexAttribPointer(
            getHandle("inputTextureCoordinate"),
            VERTICES_DATA_UV_SIZE,
            GLES20.GL_FLOAT,
            false,
            VERTICES_DATA_STRIDE_BYTES,
            VERTICES_DATA_UV_OFFSET
        )
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(texTarget, texName)
        GLES20.glUniform1i(getHandle(DEFAULT_UNIFORM_SAMPLER), 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(getHandle("position"))
        GLES20.glDisableVertexAttribArray(getHandle("inputTextureCoordinate"))
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }
}