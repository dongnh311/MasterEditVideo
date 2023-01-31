package com.dongnh.masteredit.transition.transition.simplezoom

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class SimpleZoomTransDrawer(context: Context) : AbstractDrawerTransition(context) {

    override fun getTransitionShader(context: Context) {
        transitionShader = SimpleZoomTransShader(context)
    }

    fun setZoomQuickness(zoomQuickness: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as SimpleZoomTransShader).setUZoomQuickness(zoomQuickness)
    }
}
