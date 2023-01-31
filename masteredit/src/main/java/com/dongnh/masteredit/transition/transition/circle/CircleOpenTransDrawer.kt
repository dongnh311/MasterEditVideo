package com.dongnh.masteredit.transition.transition.circle

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class CircleOpenTransDrawer(context: Context) : AbstractDrawerTransition(context) {

    override fun getTransitionShader(context: Context) {
        transitionShader = CircleOpenTransShader(context)
    }

    fun setSmoothness(smoothness: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as CircleOpenTransShader).setUSmoothness(smoothness)
    }

    fun setOpening(opening: Boolean) {
        GLES20.glUseProgram(programId)
        (transitionShader as CircleOpenTransShader).setUOpening(opening)
    }
}