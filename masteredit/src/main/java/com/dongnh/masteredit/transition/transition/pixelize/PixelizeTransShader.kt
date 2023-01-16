package com.dongnh.masteredit.transition.transition.pixelize

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class PixelizeTransShader(context: Context) : TransitionMainFragmentShader() {
    val TRANS_SHADER = "pixelize.glsl"

    val U_SQUARES_MIN = "squaresMin"
    val U_STEP = "step"

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
        addLocation(U_SQUARES_MIN, true)
        addLocation(U_STEP, true)
        loadLocation(programId)
    }

    fun setUSquaresMin(width: Int, height: Int) {
        setUniform(U_SQUARES_MIN, width, height)
    }

    fun setUStep(step: Int) {
        setUniform(U_STEP, step)
    }
}