package com.dongnh.masteredit.graph

import android.opengl.GLES20
import com.dongnh.masteredit.gl.GLFilterObject

/**
 * Project : MasterEditVideo
 * Created by DongNH on 18/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class BlurNormalBackground :
    GLFilterObject(BLUR_NORMAL_VERTEXT_SHADER, BLUR_NORMAL_FRAGMENT_SHADER) {

    companion object {
        const val BLUR_NORMAL_VERTEXT_SHADER = """
        attribute vec3 position;
        attribute vec2 inputTextureCoordinate;

        varying vec2 textureCoordinate;

        void main() {
            gl_Position = vec4(position, 1.0);
            textureCoordinate.xy = (inputTextureCoordinate.xy - 0.5) / 1.5 + 0.5;
        }
        """

        const val BLUR_NORMAL_FRAGMENT_SHADER = """
        precision highp float; 
        varying vec2 textureCoordinate; 
        uniform sampler2D inputImageTexture; 
        
        void main() {
            gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
        }
        """
    }

    var inputImageTexture = 0f

    init {
        this@BlurNormalBackground.vertexShaderSource = BLUR_NORMAL_VERTEXT_SHADER
        this@BlurNormalBackground.fragmentShaderSource = BLUR_NORMAL_FRAGMENT_SHADER
    }

    override fun onDraw() {
        GLES20.glUniform1f(getHandle("inputImageTexture"), inputImageTexture)
    }

    override fun setup() {
        super.setup()
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, getHandle("inputImageTexture"))
        GLES20.glUniform1i(getHandle("inputImageTexture"), 0)
    }

}