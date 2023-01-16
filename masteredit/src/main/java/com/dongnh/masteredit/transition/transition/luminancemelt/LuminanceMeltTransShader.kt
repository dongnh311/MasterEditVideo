package com.dongnh.masteredit.transition.transition.luminancemelt

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class LuminanceMeltTransShader(context: Context) :
    TransitionMainFragmentShader() {
    val TRANS_SHADER = "luminance_melt.glsl"

    val U_DIRECTION = "direction"
    val U_THRESHOLD = "l_threshold"
    val U_ABOVE = "above"

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
        addLocation(U_DIRECTION, true)
        addLocation(U_THRESHOLD, true)
        addLocation(U_ABOVE, true)
        loadLocation(programId)
    }

    fun setUDirection(down: Boolean) {
        setUniform(U_DIRECTION, down)
    }

    fun setUThreshold(threshold: Float) {
        setUniform(U_THRESHOLD, threshold)
    }

    fun setUAbove(above: Boolean) {
        setUniform(U_ABOVE, above)
    }
}