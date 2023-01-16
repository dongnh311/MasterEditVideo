package com.dongnh.masteredit.transition.draw

import android.opengl.GLES20
import com.dongnh.masteredit.base.AbstractShaderTransition

/**
 * Project : MasterEditVideo
 * Created by DongNH on 16/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
open class TransitionMainFragmentShader : AbstractShaderTransition() {

    val BASE_SHADER = "TransitionMainFragmentShader.glsl"

    val U_INPUT_TEXTURE = "u_InputTexture"
    val U_INPUT_TEXTURE2 = "u_InputTexture2"
    val U_PROGRESS = "progress"
    val U_RATIO = "ratio"
    val U_RATIO_2 = "ratio2"

    override fun initLocation(programId: Int) {
        addLocation(U_INPUT_TEXTURE, true)
        addLocation(U_INPUT_TEXTURE2, true)
        addLocation(U_PROGRESS, true)
        addLocation(U_RATIO, true)
        addLocation(U_RATIO_2, true)
    }

    fun setUInputTexture(textureTarget: Int, textureId: Int) {
        // bind texture
        GLES20.glActiveTexture(textureTarget)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        setUniform(U_INPUT_TEXTURE, textureTarget - GLES20.GL_TEXTURE0)
    }

    fun setUInputTexture2(textureTarget: Int, textureId: Int) {
        // bind texture
        GLES20.glActiveTexture(textureTarget)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        setUniform(U_INPUT_TEXTURE2, textureTarget - GLES20.GL_TEXTURE0)
    }

    fun setUProgress(progress: Float) {
        setUniform(U_PROGRESS, progress)
    }

    fun setURatio(ratio: Float) {
        setUniform(U_RATIO, ratio)
    }

    fun setUToRatio(ratio: Float) {
        setUniform(U_RATIO_2, ratio)
    }
}