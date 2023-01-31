package com.dongnh.masteredit.transition.transition.invertedpage

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class InvertedPageCurlTransShader(context: Context) : TransitionMainFragmentShader() {

    val TAG = "InvertedPageCurlTransShader"

    val TRANS_SHADER = "InvertedPageCurl.glsl"

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