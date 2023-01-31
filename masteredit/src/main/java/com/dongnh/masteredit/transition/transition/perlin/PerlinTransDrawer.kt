package com.dongnh.masteredit.transition.transition.perlin

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class PerlinTransDrawer(context: Context) : AbstractDrawerTransition(context) {

    override fun getTransitionShader(context: Context) {
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