package kr.brickmate.clllap.utils.openegl.transition.bounce

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class BounceTransDrawer(private val context: Context) : AbstractDrawerTransition(context) {

    override fun getTransitionShader() {
        transitionShader = BounceTransShader(context)
    }

    fun setShadowColor(red: Float, green: Float, blue: Float, alpha: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as BounceTransShader).setUShadowColor(red, green, blue, alpha)
    }

    fun setShadowHeight(shadowHeight: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as BounceTransShader).setUShadowHeight(shadowHeight)
    }

    fun setBounces(bounces: Float) {
        GLES20.glUseProgram(programId)
        (transitionShader as BounceTransShader).setUBounces(bounces)
    }
}