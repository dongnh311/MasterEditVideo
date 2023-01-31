package com.dongnh.masteredit.transition.transition.wind

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class WindTransShader(glTransitionManager: Context) : TransitionMainFragmentShader() {
    val TRANS_SHADER = "wind.glsl"

    val U_SIZE = "size"

    init {
        initShader(
            arrayOf(
                TRANSITION_FOLDER + BASE_SHADER,
                TRANSITION_FOLDER + TRANS_SHADER
            ), GLES20.GL_FRAGMENT_SHADER, glTransitionManager
        )
    }

    override fun initLocation(programId: Int) {
        super.initLocation(programId)
        addLocation(U_SIZE, true)
        loadLocation(programId)
    }

    fun setUSize(size: Float) {
        setUniform(U_SIZE, size)
    }
}
