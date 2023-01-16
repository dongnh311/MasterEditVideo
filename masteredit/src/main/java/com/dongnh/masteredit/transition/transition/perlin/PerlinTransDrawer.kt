package com.dongnh.masteredit.transition.transition.perlin

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class PerlinTransDrawer(private val context: Context) : AbstractDrawerTransition(context) {

    override fun getTransitionShader() {
        transitionShader = PerlinTransShader(context)
    }

    fun setScale(scale: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as PerlinTransShader).setUScale(scale)
    }

    fun setSmoothness(smoothness: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as PerlinTransShader).setUSmoothness(smoothness)
    }

    fun setSeed(seed: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as PerlinTransShader).setUSeed(seed)
    }
}