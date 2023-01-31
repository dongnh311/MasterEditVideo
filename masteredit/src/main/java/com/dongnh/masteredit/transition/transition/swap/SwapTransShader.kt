package com.dongnh.masteredit.transition.transition.swap

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class SwapTransShader(context: Context) : TransitionMainFragmentShader() {
    val TRANS_SHADER = "swap.glsl"

    val U_REFLECTION = "reflection"
    val U_PERSPECTIVE = "perspective"
    val U_DEPTH = "depth"

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
        addLocation(U_REFLECTION, true)
        addLocation(U_PERSPECTIVE, true)
        addLocation(U_DEPTH, true)
        loadLocation(programId)
    }

    fun setUReflection(reflection: Float) {
        setUniform(U_REFLECTION, reflection)
    }

    fun setUPerspective(perspective: Float) {
        setUniform(U_PERSPECTIVE, perspective)
    }

    fun setUDepth(depth: Float) {
        setUniform(U_DEPTH, depth)
    }
}