package com.dongnh.masteredit.transition.transition.crosszoom

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class CrossZoomTransDrawer(context: Context) : AbstractDrawerTransition(context) {

    override fun getTransitionShader(context: Context) {
        transitionShader = CrossZoomTransShader(context)
    }

    fun setStrength(strength: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as CrossZoomTransShader).setUStrength(strength)
    }
}
