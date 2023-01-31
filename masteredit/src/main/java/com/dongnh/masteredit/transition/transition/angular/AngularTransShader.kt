package com.dongnh.masteredit.transition.transition.angular

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class AngularTransShader(context: Context) : TransitionMainFragmentShader() {

    val TRANS_SHADER = "angular.glsl"

    val U_START_ANGLE = "startingAngle"

    var mUStartAngle = 0

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
        mUStartAngle = GLES20.glGetUniformLocation(programId, U_START_ANGLE)
        loadLocation(programId)
    }

    fun setUStartAngular(startAngular: Float) {
        GLES20.glUniform1f(mUStartAngle, startAngular)
    }
}