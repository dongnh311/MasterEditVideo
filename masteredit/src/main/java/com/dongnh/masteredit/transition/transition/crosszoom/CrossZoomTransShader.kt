package com.dongnh.masteredit.transition.transition.crosszoom

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class CrossZoomTransShader(context: Context) : TransitionMainFragmentShader() {
    val TRANS_SHADER = "CrossZoom.glsl"

    val U_STRENGTH = "strength"

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
        addLocation(U_STRENGTH, true)
        loadLocation(programId)
    }

    fun setUStrength(strength: Float) {
        setUniform(U_STRENGTH, strength)
    }
}
