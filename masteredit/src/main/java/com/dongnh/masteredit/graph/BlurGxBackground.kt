package com.dongnh.masteredit.graph

import android.opengl.GLES20
import com.dongnh.masteredit.gl.GLFilterObject

/**
 * Project : MasterEditVideo
 * Created by DongNH on 18/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class BlurGxBackground : GLFilterObject(BLUR_GX_VERTEXT_SHADER, BLUR_GX_FRAGMENT_SHADER) {

    companion object {
        val BLUR_GX_VERTEXT_SHADER = """
        attribute vec3 position;
        attribute vec2 inputTextureCoordinate;
        
        uniform float texelWidthOffset;
        uniform float texelHeightOffset;
        uniform float scale;
        varying vec2 blurCoordinates[15];
        void main()
        {
            gl_Position = vec4(position, 1.);
            
            vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);
            blurCoordinates[0] = inputTextureCoordinate;
            blurCoordinates[1] = inputTextureCoordinate.xy + singleStepOffset * 1.489585 * scale;
            blurCoordinates[2] = inputTextureCoordinate.xy - singleStepOffset * 1.489585 * scale;
            blurCoordinates[3] = inputTextureCoordinate.xy + singleStepOffset * 3.475713 * scale;
            blurCoordinates[4] = inputTextureCoordinate.xy - singleStepOffset * 3.475713 * scale;
            blurCoordinates[5] = inputTextureCoordinate.xy + singleStepOffset * 5.461879 * scale;
            blurCoordinates[6] = inputTextureCoordinate.xy - singleStepOffset * 5.461879 * scale;
            blurCoordinates[7] = inputTextureCoordinate.xy + singleStepOffset * 7.448104 * scale;
            blurCoordinates[8] = inputTextureCoordinate.xy - singleStepOffset * 7.448104 * scale;
            blurCoordinates[9] = inputTextureCoordinate.xy + singleStepOffset * 9.434408 * scale;
            blurCoordinates[10] = inputTextureCoordinate.xy - singleStepOffset * 9.434408 * scale;
            blurCoordinates[11] = inputTextureCoordinate.xy + singleStepOffset * 11.420812 * scale;
            blurCoordinates[12] = inputTextureCoordinate.xy - singleStepOffset * 11.420812 * scale;
            blurCoordinates[13] = inputTextureCoordinate.xy + singleStepOffset * 13.407332 * scale;
            blurCoordinates[14] = inputTextureCoordinate.xy - singleStepOffset * 13.407332 * scale;
        }
    """.trimIndent()

        const val BLUR_GX_FRAGMENT_SHADER = """
        precision highp float;
        varying highp vec2 blurCoordinates[15];
        uniform sampler2D inputImageTexture; 
       
        void main() {
        
            lowp vec4 sum = vec4(0.0);
        
            sum += texture2D(inputImageTexture, blurCoordinates[0]) * 0.067540;
            sum += texture2D(inputImageTexture, blurCoordinates[1]) * 0.130499;
            sum += texture2D(inputImageTexture, blurCoordinates[2]) * 0.130499;
            sum += texture2D(inputImageTexture, blurCoordinates[3]) * 0.113686;
            sum += texture2D(inputImageTexture, blurCoordinates[4]) * 0.113686;
            sum += texture2D(inputImageTexture, blurCoordinates[5]) * 0.088692;
            sum += texture2D(inputImageTexture, blurCoordinates[6]) * 0.088692;
            sum += texture2D(inputImageTexture, blurCoordinates[7]) * 0.061965;
            sum += texture2D(inputImageTexture, blurCoordinates[8]) * 0.061965;
            sum += texture2D(inputImageTexture, blurCoordinates[9]) * 0.038768;
            sum += texture2D(inputImageTexture, blurCoordinates[10]) * 0.038768;
            sum += texture2D(inputImageTexture, blurCoordinates[11]) * 0.021721;
            sum += texture2D(inputImageTexture, blurCoordinates[12]) * 0.021721;
            sum += texture2D(inputImageTexture, blurCoordinates[13]) * 0.010898;
            sum += texture2D(inputImageTexture, blurCoordinates[14]) * 0.010898;
            gl_FragColor = sum;
        }
        """
    }

    var inputImageTexture = 0f

    var texelWidthOffset = 0f
    var texelHeightOffset = 0f
    var scaleVss = 1.4f

    var textureId = -1

    init {
        this@BlurGxBackground.vertexShaderSource = BLUR_GX_VERTEXT_SHADER
        this@BlurGxBackground.fragmentShaderSource = BLUR_GX_FRAGMENT_SHADER
    }

    override fun onDraw() {
        GLES20.glUniform1f(getHandle("texelWidthOffset"), texelWidthOffset)
        GLES20.glUniform1f(getHandle("texelHeightOffset"), texelHeightOffset)
        GLES20.glUniform1f(getHandle("scale"), scaleVss)
        GLES20.glUniform1f(getHandle("inputImageTexture"), inputImageTexture)
        GLES20.glViewport(
            0,
            0,
            360,
            640
        )
    }

    override fun setup() {
        super.setup()
        GLES20.glUniform1f(
            GLES20.glGetUniformLocation(programCurrent, "texelWidthOffset"),
            texelWidthOffset
        )
        GLES20.glUniform1f(
            GLES20.glGetUniformLocation(programCurrent, "texelHeightOffset"),
            texelHeightOffset
        )
        GLES20.glUniform1f(GLES20.glGetUniformLocation(programCurrent, "scale"), scaleVss)
        GLES20.glUniform1f(
            GLES20.glGetUniformLocation(programCurrent, "inputImageTexture"),
            inputImageTexture
        )
        GLES20.glViewport(
            0,
            0,
            360,
            640
        )
    }
}