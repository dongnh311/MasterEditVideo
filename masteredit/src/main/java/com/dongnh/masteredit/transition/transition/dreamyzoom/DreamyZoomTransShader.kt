package com.dongnh.masteredit.transition.transition.dreamyzoom

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class DreamyZoomTransShader(context: Context) :
    TransitionMainFragmentShader() {
    val TRANS_SHADER = "DreamyZoom.glsl"

    val U_ROTATION = "rotation"
    val U_SCALE = "scale"

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
        addLocation(U_ROTATION, true)
        addLocation(U_SCALE, true)
        loadLocation(programId)
    }

    fun setURotation(rotation: Float) {
        setUniform(U_ROTATION, rotation)
    }

    fun setUScale(scale: Float) {
        setUniform(U_SCALE, scale)
    }
}
