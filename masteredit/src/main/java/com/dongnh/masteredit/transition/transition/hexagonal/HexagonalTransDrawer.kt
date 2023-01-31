package com.dongnh.masteredit.transition.transition.hexagonal

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class HexagonalTransDrawer(context: Context) : AbstractDrawerTransition(context) {

    override fun getTransitionShader(context: Context) {
        transitionShader = HexagonalizeTransShader(context)
    }

    fun setStep(step: Int) {
        GLES20.glUseProgram(programId)
        (transitionShader as HexagonalizeTransShader).setUStep(step)
    }

    fun setHorizontalHexagons(horizontalHexagons: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as HexagonalizeTransShader).setUHorizontalHexagons(horizontalHexagons)
    }
}