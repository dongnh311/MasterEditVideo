package com.dongnh.masteredit.eglcore

import android.opengl.GLSurfaceView
import timber.log.Timber
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
open class DefaultContextFactory(version: Int) : GLSurfaceView.EGLContextFactory {

    val TAG = "DefaultContextFactory"

    private var eglContextClientVersion = 0

    init {
        eglContextClientVersion = version
    }

    val EGL_CONTEXT_CLIENT_VERSION = 0x3098

    override fun createContext(egl: EGL10, display: EGLDisplay?, config: EGLConfig?): EGLContext? {
        val attribList: IntArray? = if (eglContextClientVersion != 0) {
            intArrayOf(EGL_CONTEXT_CLIENT_VERSION, eglContextClientVersion, EGL10.EGL_NONE)
        } else {
            null
        }
        return egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, attribList)
    }

    override fun destroyContext(egl: EGL10, display: EGLDisplay, context: EGLContext) {
        if (!egl.eglDestroyContext(display, context)) {
            Timber.e("display:$display context: $context")
            throw RuntimeException("eglDestroyContext" + egl.eglGetError())
        }
    }

}