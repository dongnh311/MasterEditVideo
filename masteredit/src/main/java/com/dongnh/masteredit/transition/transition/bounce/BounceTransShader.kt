package kr.brickmate.clllap.utils.openegl.transition.bounce

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class BounceTransShader(context: Context) : TransitionMainFragmentShader() {
    val TAG = "AngularTransShader"

    val TRANS_SHADER = "Bounce.glsl"

    val U_SHADOW_COLOR = "shadow_colour"
    val U_SHADOW_HEIGHT = "shadow_height"
    val U_BOUNCES = "bounces"

    init {
        initShader(
            arrayOf(
                TRANSITION_FOLDER + BASE_SHADER,
                TRANSITION_FOLDER + TRANS_SHADER
            ), GLES20.GL_FRAGMENT_SHADER, context
        )
    }

    override fun initLocation(programId: Int) {
        super.initLocation(programId)
        addLocation(U_SHADOW_COLOR, true)
        addLocation(U_SHADOW_HEIGHT, true)
        addLocation(U_BOUNCES, true)
        loadLocation(programId)
    }

    fun setUShadowColor(red: Float, green: Float, blue: Float, alpha: Float) {
        setUniform(U_SHADOW_COLOR, red, green, blue, alpha)
    }

    fun setUShadowHeight(shadowHeight: Float) {
        setUniform(U_SHADOW_HEIGHT, shadowHeight)
    }

    fun setUBounces(bounces: Float) {
        setUniform(U_BOUNCES, bounces)
    }
}