package com.dongnh.masteredit.graph

import android.opengl.GLES20
import com.dongnh.masteredit.gl.GLFilterObject

/**
 * Project : MasterEditVideo
 * Created by DongNH on 18/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class FourScreenGraph : GLFilterObject(DEFAULT_VERTEX_SHADER, FOUR_FRAGMENT_SHADER) {

    companion object {
        const val FOUR_FRAGMENT_SHADER = """precision highp float;
        uniform sampler2D inputImageTexture;
        varying highp vec2 textureCoordinate;
        void main() {
            int row = int(textureCoordinate.x * 2.0);
            int col = int(textureCoordinate.y * 2.0);
            
            vec2 textureCoordinateToUse = textureCoordinate;
            
            textureCoordinateToUse.x = (textureCoordinate.x - float(row) / 2.0) * 2.0;
            textureCoordinateToUse.y = (textureCoordinate.y - float(col) / 2.0) * 2.0;
      
            gl_FragColor=texture2D(inputImageTexture, textureCoordinateToUse);
        }
        """
    }

    var inputImageTexture = 100f

    init {
        this@FourScreenGraph.vertexShaderSource = DEFAULT_VERTEX_SHADER
        this@FourScreenGraph.fragmentShaderSource = FOUR_FRAGMENT_SHADER
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
