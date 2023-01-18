package com.dongnh.masteredit.graph

import android.opengl.GLES20
import com.dongnh.masteredit.gl.GLFilterObject

/**
 * Project : MasterEditVideo
 * Created by DongNH on 18/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class NiceScreenGraph : GLFilterObject(DEFAULT_VERTEX_SHADER, NICE_FRAGMENT_SHADER) {

    companion object {
        const val NICE_FRAGMENT_SHADER = """precision highp float;
        uniform sampler2D inputImageTexture;
        varying highp vec2 textureCoordinate;
        void main() {
            int row = int(textureCoordinate.x * 3.0);
            int col = int(textureCoordinate.y * 3.0);
            
            vec2 textureCoordinateToUse = textureCoordinate;
            
            textureCoordinateToUse.x = (textureCoordinate.x - float(row) / 3.0) * 3.0;
            textureCoordinateToUse.y = (textureCoordinate.y - float(col) / 3.0) * 3.0;
    
            gl_FragColor=texture2D(inputImageTexture, textureCoordinateToUse);
        }
        """
    }

    var inputImageTexture = 100f

    init {
        this@NiceScreenGraph.vertexShaderSource = DEFAULT_VERTEX_SHADER
        this@NiceScreenGraph.fragmentShaderSource = NICE_FRAGMENT_SHADER
    }

    override fun onDraw() {
        GLES20.glUniform1f(getHandle("inputImageTexture"), inputImageTexture)
    }

    override fun setup() {
        super.setup()
        GLES20.glUniform1f(
            GLES20.glGetUniformLocation(programCurrent, "inputImageTexture"),
            inputImageTexture
        )
    }
}