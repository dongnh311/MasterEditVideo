package com.dongnh.masteredit.transition.transition.simplezoom

import android.content.Context
import android.opengl.GLES20
import com.dongnh.masteredit.transition.draw.TransitionMainFragmentShader

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class SimpleZoomTransShader(context: Context) :
    TransitionMainFragmentShader() {

    val TRANS_SHADER = "SimpleZoom.glsl"

    val U_ZOOM_QUICKNESS = "zoom_quickness"

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
        addLocation(U_ZOOM_QUICKNESS, true)
        loadLocation(programId)
    }

    fun setUZoomQuickness(zoomQuickness: Float) {
        setUniform(U_ZOOM_QUICKNESS, zoomQuickness)
    }
}