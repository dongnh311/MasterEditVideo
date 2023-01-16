package com.dongnh.masteredit.transition.transition.angular

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class AngularTransDrawer(private val context: Context) : AbstractDrawerTransition(context) {

    override fun getTransitionShader() {
        transitionShader = AngularTransShader(context)
    }

    fun setStartAngular(startAngular: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as AngularTransShader).setUStartAngular(startAngular)
    }
}