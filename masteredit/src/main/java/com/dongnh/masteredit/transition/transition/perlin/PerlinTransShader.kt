package com.dongnh.masteredit.transition.transition.perlin

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class PerlinTransShader(context: Context) : TransitionMainFragmentShader() {
    val TRANS_SHADER = "perlin.glsl"

    val U_SCALE = "scale"
    val U_SMOOTHNESS = "smoothness"
    val U_SEED = "seed"

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
        addLocation(U_SCALE, true)
        addLocation(U_SMOOTHNESS, true)
        addLocation(U_SEED, true)
        loadLocation(programId)
    }

    fun setUScale(scale: Float) {
        setUniform(U_SCALE, scale)
    }

    fun setUSmoothness(smoothness: Float) {
        setUniform(U_SMOOTHNESS, smoothness)
    }

    fun setUSeed(seed: Float) {
        setUniform(U_SEED, seed)
    }
}