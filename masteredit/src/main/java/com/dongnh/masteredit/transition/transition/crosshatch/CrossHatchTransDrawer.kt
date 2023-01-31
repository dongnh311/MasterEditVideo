package com.dongnh.masteredit.transition.transition.crosshatch

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class CrossHatchTransDrawer(context: Context) : AbstractDrawerTransition(context) {

    override fun getTransitionShader(context: Context) {
        transitionShader = CrossHatchTransShader(context)
    }

    fun setCenter(centerX: Float, centerY: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as CrossHatchTransShader).setUCenter(centerX, centerY)
    }

    fun setThreshold(threshold: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as CrossHatchTransShader).setUThreshold(threshold)
    }

    fun setFadeEdge(fadeEdge: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as CrossHatchTransShader).setUFadeEdge(fadeEdge)
    }
}