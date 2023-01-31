package com.dongnh.masteredit.gl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
abstract class GLFrameBufferObjectRenderer : GLSurfaceView.Renderer {
    // Object draw
    private var framebufferObject: GLFramebufferObject? = null

    // List filter
    var listFilter = mutableListOf<GLFilterObject>()

    // Normal filter to draw
    private var normalShader: GLFilterObject? = null

    // Lock thread
    private val runOnDraw: Queue<Runnable> = LinkedList()

    /**
     * When surface is created, call back to parent update or prepare to view
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        framebufferObject = GLFramebufferObject()
        normalShader = GLFilterObject()
        normalShader!!.setup()
        listFilter.forEach {
            it.setup()
        }
        onSurfaceCreated(config)
    }

    /**
     * Init new with and height for surface
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        framebufferObject?.setup(width, height)
        normalShader?.setFrameSize(width, height)
        listFilter.forEach {
            it.setFrameSize(width, height)
        }
        onSurfaceChanged(width, height)
        framebufferObject?.getHeight().let {
            framebufferObject?.getWidth()?.let { width ->
                GLES20.glViewport(
                    0, 0,
                    width, it!!
                )
            }
        }
    }

    /**
     * Draw frame
     */
    override fun onDrawFrame(gl: GL10?) {
        synchronized(runOnDraw) {
            while (!runOnDraw.isEmpty()) {
                @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                runOnDraw.poll().run()
            }
        }
        framebufferObject?.enable()
        onDrawFrame(framebufferObject)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        framebufferObject?.getTexName()?.let {
            normalShader?.draw(it, null)
            listFilter.forEach { glFilter ->
                glFilter.draw(it, null)
            }
        }
    }

    @Throws(Throwable::class)
    protected fun finalize() {
    }

    /**
     * Clear all item draw
     */
    fun releaseDraw() {
        framebufferObject = null
        normalShader = null
        listFilter.clear()
    }

    abstract fun onSurfaceCreated(config: EGLConfig?)
    abstract fun onSurfaceChanged(width: Int, height: Int)
    abstract fun onDrawFrame(fbo: GLFramebufferObject?)
}

