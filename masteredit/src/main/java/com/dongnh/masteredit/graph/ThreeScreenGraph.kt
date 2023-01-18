package com.dongnh.masteredit.graph

import android.opengl.GLES20
import com.dongnh.masteredit.gl.GLFilterObject

/**
 * Project : MasterEditVideo
 * Created by DongNH on 18/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class ThreeScreenGraph : GLFilterObject(DEFAULT_VERTEX_SHADER, THREE_FRAGMENT_SHADER) {

    companion object {
        const val THREE_FRAGMENT_SHADER = """precision highp float;
        uniform sampler2D inputImageTexture;
        varying highp vec2 textureCoordinate;
        void main() {
            int col = int(textureCoordinate.y * 3.0);
            vec2 textureCoordinateToUse = textureCoordinate;
            textureCoordinateToUse.y = (textureCoordinate.y - float(col) / 3.0) * 3.0;
            textureCoordinateToUse.y = textureCoordinateToUse.y/1280.0*320.0+1.0/3.0;
            gl_FragColor=texture2D(inputImageTexture, textureCoordinateToUse);
        }
        """
    }

    var inputImageTexture = 100f

    init {
        this@ThreeScreenGraph.vertexShaderSource = DEFAULT_VERTEX_SHADER
        this@ThreeScreenGraph.fragmentShaderSource = THREE_FRAGMENT_SHADER
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

