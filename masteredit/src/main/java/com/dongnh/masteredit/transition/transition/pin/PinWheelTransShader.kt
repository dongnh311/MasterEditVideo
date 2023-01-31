package com.dongnh.masteredit.transition.transition.pin

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class PinWheelTransShader(context: Context) : TransitionMainFragmentShader() {
    val TRANS_SHADER = "pinwheel.glsl"

    val U_SPEED = "speed"

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
        addLocation(U_SPEED, true)
        loadLocation(programId)
    }

    fun setUSpeed(speed: Float) {
        setUniform(U_SPEED, speed)
    }
}