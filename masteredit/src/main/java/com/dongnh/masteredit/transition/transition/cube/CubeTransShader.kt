package com.dongnh.masteredit.transition.transition.cube

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class CubeTransShader(context: Context) : TransitionMainFragmentShader() {
    val TRANS_SHADER = "cube.glsl"

    val U_PERSPECTIVE = "persp"
    val U_UNZOOM = "unzoom"
    val U_REFLECTION = "reflection"
    val U_FLOATING = "floating"

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
        addLocation(U_PERSPECTIVE, true)
        addLocation(U_UNZOOM, true)
        addLocation(U_REFLECTION, true)
        addLocation(U_FLOATING, true)
        loadLocation(programId)
    }

    fun setUPerspective(perspective: Float) {
        setUniform(U_PERSPECTIVE, perspective)
    }

    fun setUUnzoom(unzoom: Float) {
        setUniform(U_UNZOOM, unzoom)
    }

    fun setUReflection(reflection: Float) {
        setUniform(U_REFLECTION, reflection)
    }

    fun setUFloating(floating: Float) {
        setUniform(U_FLOATING, floating)
    }
}