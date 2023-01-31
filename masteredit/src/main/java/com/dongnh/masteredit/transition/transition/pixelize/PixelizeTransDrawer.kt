package com.dongnh.masteredit.transition.transition.pixelize

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class PixelizeTransDrawer(context: Context) : AbstractDrawerTransition(context) {

    override fun getTransitionShader(context: Context) {
        transitionShader = PixelizeTransShader(context)
    }

    fun setSquaresMin(width: Int, height: Int) {
        GLES20.glUseProgram(programId)
        (transitionShader as PixelizeTransShader).setUSquaresMin(width, height)
    }

    fun setStep(step: Int) {
        GLES20.glUseProgram(programId)
        (transitionShader as PixelizeTransShader).setUStep(step)
    }
}