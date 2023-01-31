package com.dongnh.masteredit.transition.transition.pin

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class PinWheelTransDrawer(context: Context) : AbstractDrawerTransition(context) {

    override fun getTransitionShader(context: Context) {
        transitionShader = PinWheelTransShader(context)
    }

    fun setSpeed(speed: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as PinWheelTransShader).setUSpeed(speed)
    }
}