package com.dongnh.masteredit.eglcore

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLDisplay

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
open class DefaultConfigChooser : GLSurfaceView.EGLConfigChooser {

    lateinit var configSpec: IntArray
    var redSize = 0
    var greenSize = 0
    var blueSize = 0
    var alphaSize = 0
    var depthSize = 0
    var stencilSize = 0

    constructor(version: Int) : this(true, version)

    constructor(withDepthBuffer: Boolean, version: Int) : this(
        8,
        8,
        8,
        0,
        if (withDepthBuffer) 16 else 0,
        0,
        version
    )

    constructor(
        redSize: Int,
        greenSize: Int,
        blueSize: Int,
        alphaSize: Int,
        depthSize: Int,
        stencilSize: Int,
        version: Int
    ) {
        this.configSpec = filterConfigSpec(
            intArrayOf(
                EGL10.EGL_RED_SIZE, redSize,
                EGL10.EGL_GREEN_SIZE, greenSize,
                EGL10.EGL_BLUE_SIZE, blueSize,
                EGL10.EGL_ALPHA_SIZE, alphaSize,
                EGL10.EGL_DEPTH_SIZE, depthSize,
                EGL10.EGL_STENCIL_SIZE, stencilSize,
                EGL10.EGL_NONE
            ), version
        )
        this.redSize = redSize
        this.greenSize = greenSize
        this.blueSize = blueSize
        this.alphaSize = alphaSize
        this.depthSize = depthSize
        this.stencilSize = stencilSize
    }

    val EGL_OPENGL_ES2_BIT = 4

    fun filterConfigSpec(configSpec: IntArray, version: Int): IntArray {
        if (version != 2) {
            return configSpec
        }
        val len = configSpec.size
        val newConfigSpec = IntArray(len + 2)
        System.arraycopy(configSpec, 0, newConfigSpec, 0, len - 1)
        newConfigSpec[len - 1] = EGL10.EGL_RENDERABLE_TYPE
        newConfigSpec[len] = EGL_OPENGL_ES2_BIT
        newConfigSpec[len + 1] = EGL10.EGL_NONE
        return newConfigSpec
    }

    //////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////
    override fun chooseConfig(egl: EGL10, display: EGLDisplay?): EGLConfig {
        // 要求されている仕様から使用可能な構成の数を抽出します。
        val num_config = IntArray(1)
        require(
            egl.eglChooseConfig(
                display,
                configSpec,
                null,
                0,
                num_config
            )
        ) { "eglChooseConfig failed" }
        val config_size = num_config[0]
        require(config_size > 0) { "No configs match configSpec" }

        // 実際の構成を抽出します。
        val configs = arrayOfNulls<EGLConfig>(config_size)
        require(
            egl.eglChooseConfig(
                display,
                configSpec,
                configs,
                config_size,
                num_config
            )
        ) { "eglChooseConfig#2 failed" }
        return chooseConfig(egl, display!!, configs)
            ?: throw IllegalArgumentException("No config chosen")
    }

    private fun chooseConfig(
        egl: EGL10,
        display: EGLDisplay,
        configs: Array<EGLConfig?>
    ): EGLConfig? {
        for (config in configs) {
            val d: Int = findConfigAttrib(egl, display, config!!, EGL10.EGL_DEPTH_SIZE, 0)
            val s: Int = findConfigAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE, 0)
            if (d >= depthSize && s >= stencilSize) {
                val r: Int = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE, 0)
                val g: Int = findConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE, 0)
                val b: Int = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE, 0)
                val a: Int = findConfigAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE, 0)
                if (r == redSize && g == greenSize && b == blueSize && a == alphaSize) {
                    return config
                }
            }
        }
        return null
    }

    open fun findConfigAttrib(
        egl: EGL10,
        display: EGLDisplay,
        config: EGLConfig,
        attribute: Int,
        defaultValue: Int
    ): Int {
        val value = IntArray(1)
        return if (egl.eglGetConfigAttrib(display, config, attribute, value)) {
            value[0]
        } else defaultValue
    }
}