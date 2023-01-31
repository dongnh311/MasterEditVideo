package com.dongnh.masteredit.transition.transition.directional

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class DirectionalWipeTransDrawer(context: Context) : AbstractDrawerTransition(context) {

    override fun getTransitionShader(context: Context) {
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