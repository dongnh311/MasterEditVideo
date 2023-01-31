package com.dongnh.masteredit.transition.transition.circle

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class CircleOpenTransShader(context: Context) : TransitionMainFragmentShader() {
    val TRANS_SHADER = "circleopen.glsl"

    val U_SMOOTHNESS = "smoothness"
    val U_OPENING = "opening"

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
        addLocation(U_SMOOTHNESS, true)
        addLocation(U_OPENING, true)
        loadLocation(programId)
    }

    fun setUSmoothness(smoothness: Float) {
        setUniform(U_SMOOTHNESS, smoothness)
    }

    fun setUOpening(opening: Boolean) {
        setUniform(U_OPENING, opening)
    }
}