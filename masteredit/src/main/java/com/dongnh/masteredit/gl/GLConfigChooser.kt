package com.dongnh.masteredit.gl

import com.dongnh.masteredit.eglcore.DefaultConfigChooser

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class GLConfigChooser(withDepthBuffer: Boolean) :
    DefaultConfigChooser(withDepthBuffer, EGL_CONTEXT_CLIENT_VERSION) {

    companion object {
        val EGL_CONTEXT_CLIENT_VERSION = 2
    }

}