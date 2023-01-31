package com.dongnh.masteredit.transition.transition.angular

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class AngularTransDrawer(context: Context) : AbstractDrawerTransition(context) {

    override fun getTransitionShader(context: Context) {
        transitionShader = AngularTransShader(context)
    }

    fun setStartAngular(startAngular: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as AngularTransShader).setUStartAngular(startAngular)
    }
}