package com.dongnh.masteredit.graph

import android.opengl.GLES20
import com.dongnh.masteredit.gl.GLFilterObject

/**
 * Project : MasterEditVideo
 * Created by DongNH on 18/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class BlurBlendBackground : GLFilterObject(BLUR_BLEND_VERTEXT_SHADER, BLUR_BLEND_FRAGMENT_SHADER) {

    companion object {
        val BLUR_BLEND_VERTEXT_SHADER = """
        attribute vec3 position;
        attribute vec2 inputTextureCoordinate;

        varying vec2 textureCoordinate;

        void main() {
            gl_Position = vec4(position, 1.0);
            textureCoordinate = inputTextureCoordinate.xy;
        }
    """.trimIndent()

        const val BLUR_BLEND_FRAGMENT_SHADER = """
        precision highp float; 
        varying vec2 textureCoordinate;
        uniform sampler2D inputImageTexture;
        uniform sampler2D inputImageTextureBlurred;
        
        void main() {
            int col = int(textureCoordinate.y * 3.0);
            vec2 textureCoordinateToUse = textureCoordinate;
            textureCoordinateToUse.y = (textureCoordinate.y - float(col) / 3.0) * 3.0;
            textureCoordinateToUse.y = textureCoordinateToUse.y / 3.0 + 1.0 / 3.0;
            if (col == 1)
                gl_FragColor = texture2D(inputImageTexture, textureCoordinateToUse);
            else 
                gl_FragColor = texture2D(inputImageTextureBlurred, textureCoordinate);
        }
        """
    }

    var inputImageTexture = 0f
    var inputImageTextureBlurred = 0f

    init {
        this@BlurBlendBackground.vertexShaderSource = BLUR_BLEND_VERTEXT_SHADER
        this@BlurBlendBackground.fragmentShaderSource = BLUR_BLEND_FRAGMENT_SHADER
    }

    override fun onDraw() {
        GLES20.glUniform1f(getHandle("inputImageTexture"), inputImageTexture)
        GLES20.glUniform1f(getHandle("inputImageTextureBlurred"), inputImageTextureBlurred)

        GLES20.glUniform1i(getHandle("inputImageTexture"), 3)
    }

    override fun setup() {
        super.setup()
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3 + 1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, getHandle("inputImageTexture"))
        GLES20.glUniform1i(getHandle("inputImageTexture"), GLES20.GL_TEXTURE3 + 1)
    }
}