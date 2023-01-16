package com.dongnh.masteredit.transition.transition.cube

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class CubeTransDrawer(private val context: Context) : AbstractDrawerTransition(context) {

    override fun getTransitionShader() {
        transitionShader = CubeTransShader(context)
    }

    fun setPerspective(perspective: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as CubeTransShader).setUPerspective(perspective)
    }

    fun setUnzoom(unzoom: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as CubeTransShader).setUUnzoom(unzoom)
    }

    fun setReflection(reflection: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as CubeTransShader).setUReflection(reflection)
    }

    fun setFloating(floating: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as CubeTransShader).setUFloating(floating)
    }
}