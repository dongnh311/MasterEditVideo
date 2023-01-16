package com.dongnh.masteredit.transition.transition.directional

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class DirectionalWipeTransShader(glTransitionManager: Context) : TransitionMainFragmentShader() {
    val TRANS_SHADER = "directionalwipe.glsl"

    val U_DIRECTIONAL = "direction"
    val U_SMOOTHNESS = "smoothness"

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
        addLocation(U_DIRECTIONAL, true)
        addLocation(U_SMOOTHNESS, true)
        loadLocation(programId)
    }

    fun setUDirectional(directionalX: Float, directionalY: Float) {
        setUniform(U_DIRECTIONAL, directionalX, directionalY)
    }

    fun setUSmoothness(smoothness: Float) {
        setUniform(U_SMOOTHNESS, smoothness)
    }
}