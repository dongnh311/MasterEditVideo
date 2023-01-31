package com.dongnh.masteredit.transition.transition.pin

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
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