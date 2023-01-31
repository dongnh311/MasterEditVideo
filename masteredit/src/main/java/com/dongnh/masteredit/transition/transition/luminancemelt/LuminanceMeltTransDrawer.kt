package com.dongnh.masteredit.transition.transition.luminancemelt

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class LuminanceMeltTransDrawer(context: Context) : AbstractDrawerTransition(context) {

    override fun getTransitionShader(context: Context) {
        transitionShader = LuminanceMeltTransShader(context)
    }

    fun setDirection(down: Boolean) {
        GLES20.glUseProgram(programId)
        (transitionShader as LuminanceMeltTransShader).setUDirection(down)
    }

    fun setThreshold(threshold: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as LuminanceMeltTransShader).setUThreshold(threshold)
    }

    fun setAbove(above: Boolean) {
        GLES20.glUseProgram(programId)
        (transitionShader as LuminanceMeltTransShader).setUAbove(above)
    }
}