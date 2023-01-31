package com.dongnh.masteredit.transition.transition.swap

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class SwapTransDrawer(context: Context) : AbstractDrawerTransition(context) {

    override fun getTransitionShader(context: Context) {
        transitionShader = SwapTransShader(context)
    }

    fun setReflection(reflection: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as SwapTransShader).setUReflection(reflection)
    }

    fun setPerspective(perspective: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as SwapTransShader).setUPerspective(perspective)
    }

    fun setDepth(depth: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as SwapTransShader).setUDepth(depth)
    }
}