package com.dongnh.masteredit.filter

import android.opengl.GLES20
import com.dongnh.masteredit.const.CONTRAST_FRAGMENT_SHADER
import com.dongnh.masteredit.gl.GLFilterObject

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class GLContrastFilterObject: GLFilterObject() {

    init {
        this@GLContrastFilterObject.fragmentShaderSource = CONTRAST_FRAGMENT_SHADER
    }

    var contrast = 1.2f

    override fun onDraw() {
        GLES20.glUniform1f(getHandle("contrast"), contrast)
    }
}