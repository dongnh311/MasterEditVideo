package com.dongnh.masteredit.transition.transition.wipeleft

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class WipeLeftTransShader(context: Context) : TransitionMainFragmentShader() {
    val TRANS_SHADER = "wipeLeft.glsl"

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
        loadLocation(programId)
    }
}
