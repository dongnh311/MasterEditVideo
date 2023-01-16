package com.dongnh.masteredit.transition.transition.crosshatch

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class CrossHatchTransShader(context: Context) :
    TransitionMainFragmentShader() {
    val TRANS_SHADER = "crosshatch.glsl"

    val U_CENTER = "center"
    val U_THRESHOLD = "threshold"
    val U_FADE_EDGE = "fadeEdge"

    init {
        initShader(
            arrayOf(
                TRANSITION_FOLDER + BASE_SHADER,
                TRANSITION_FOLDER + TRANS_SHADER
            ), GLES20.GL_FRAGMENT_SHADER, context
        )
    }

    override fun initLocation(programId: Int) {
        super.initLocation(programId)
        addLocation(U_CENTER, true)
        addLocation(U_THRESHOLD, true)
        addLocation(U_FADE_EDGE, true)
        loadLocation(programId)
    }

    fun setUCenter(centerX: Float, centerY: Float) {
        setUniform(U_CENTER, centerX, centerY)
    }

    fun setUThreshold(threshold: Float) {
        setUniform(U_THRESHOLD, threshold)
    }

    fun setUFadeEdge(fadeEdge: Float) {
        setUniform(U_FADE_EDGE, fadeEdge)
    }
}