package com.dongnh.masteredit.transition.transition.wind

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class WindTransDrawer(private val context: Context) : AbstractDrawerTransition(context) {

    override fun getTransitionShader() {
        transitionShader = WindTransShader(context)
    }

    fun setSize(size: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as WindTransShader).setUSize(size)
    }
}
