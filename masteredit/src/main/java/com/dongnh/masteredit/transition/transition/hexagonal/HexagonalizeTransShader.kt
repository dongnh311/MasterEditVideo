package com.dongnh.masteredit.transition.transition.hexagonal

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class HexagonalizeTransShader(context: Context) : TransitionMainFragmentShader() {
    val TRANS_SHADER = "hexagonalize.glsl"

    val U_STEPS = "steps"
    val U_HORIZONTAL_HEX = "horizontalHexagons"

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
        addLocation(U_STEPS, true)
        addLocation(U_HORIZONTAL_HEX, true)
        loadLocation(programId)
    }

    fun setUStep(step: Int) {
        setUniform(U_STEPS, step)
    }

    fun setUHorizontalHexagons(horizontalHexagons: Float) {
        setUniform(U_HORIZONTAL_HEX, horizontalHexagons)
    }
}