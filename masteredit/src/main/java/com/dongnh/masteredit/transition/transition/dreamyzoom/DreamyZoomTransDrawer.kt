package com.dongnh.masteredit.transition.transition.dreamyzoom

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class DreamyZoomTransDrawer(private val context: Context) :
    AbstractDrawerTransition(context) {

    override fun getTransitionShader() {
        transitionShader = DreamyZoomTransShader(context)
    }

    fun setRotation(rotation: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as DreamyZoomTransShader).setURotation(rotation)
    }

    fun setScale(scale: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as DreamyZoomTransShader).setUScale(scale)
    }
}
