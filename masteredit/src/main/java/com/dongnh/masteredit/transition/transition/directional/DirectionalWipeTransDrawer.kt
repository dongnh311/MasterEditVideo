package com.dongnh.masteredit.transition.transition.directional

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class DirectionalWipeTransDrawer(private val context: Context) : AbstractDrawerTransition(context) {

    override fun getTransitionShader() {
        transitionShader = DirectionalWipeTransShader(context)
    }

    fun setSmoothness(smoothness: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as DirectionalWipeTransShader).setUSmoothness(smoothness)
    }

    fun setDirection(directionX: Float, directionY: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as DirectionalWipeTransShader).setUDirectional(directionX, directionY)
    }
}