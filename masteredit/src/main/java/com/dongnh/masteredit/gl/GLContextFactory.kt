package com.dongnh.masteredit.gl

import com.dongnh.masteredit.eglcore.DefaultContextFactory

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class GLContextFactory : DefaultContextFactory(EGL_CONTEXT_CLIENT_VERSION) {

    companion object {
        val EGL_CONTEXT_CLIENT_VERSION = 2
    }
}
