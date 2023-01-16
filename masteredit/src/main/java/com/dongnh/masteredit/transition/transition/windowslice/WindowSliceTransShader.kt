package com.dongnh.masteredit.transition.transition.windowslice

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class WindowSliceTransShader(context: Context) :
    TransitionMainFragmentShader() {

    val TAG = "WindowSliceTransShader"

    val TRANS_SHADER = "windowslice.glsl"

    val U_COUNT = "count"
    val U_SMOOTHNESS = "smoothness"

    var count = 0
    private var smoothness: Int = 0

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
        count = GLES20.glGetUniformLocation(programId, U_COUNT)
        smoothness = GLES20.glGetUniformLocation(programId, U_SMOOTHNESS)
        loadLocation(programId)
    }

    fun setUCount(count: Float) {
        GLES20.glUniform1f(this.count, count)
    }

    fun setUSmoothness(smoothness: Float) {
        GLES20.glUniform1f(this.smoothness, smoothness)
    }
}