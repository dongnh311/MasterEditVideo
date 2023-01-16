package com.dongnh.masteredit.transition.transition.windowslice

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class WindowSliceTransDrawer(private val context: Context) :
    AbstractDrawerTransition(context) {

    override fun getTransitionShader() {
        transitionShader = WindowSliceTransShader(context)
    }

    fun setCount(count: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as WindowSliceTransShader).setUCount(count)
    }

    fun setSmoothness(smoothness: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as WindowSliceTransShader).setUSmoothness(smoothness)
    }
}