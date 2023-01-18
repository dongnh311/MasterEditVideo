package com.dongnh.masteredit.gl

import android.opengl.GLES20
import com.dongnh.masteredit.utils.glutils.EglUtil
import timber.log.Timber

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
open class GLFilterObject() {

    companion object {
        val DEFAULT_VERTEX_SHADER = """
        attribute highp vec3 position;
        attribute highp vec2 inputTextureCoordinate;
        varying highp vec2 textureCoordinate;
        void main() {
            gl_Position = vec4(position, 1.0);
            textureCoordinate = inputTextureCoordinate;
        }
        
        """.trimIndent()

        val DEFAULT_FRAGMENT_SHADER = """
        precision mediump float;
        varying highp vec2 textureCoordinate;
        uniform lowp sampler2D inputImageTexture;
        void main() {
            gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
        }
        
        """.trimIndent()

        private val VERTICES_DATA = floatArrayOf( // X, Y, Z, U, V
            -1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
            1.0f, -1.0f, 0.0f, 1.0f, 0.0f
        )

        const val FLOAT_SIZE_BYTES = 4
        const val VERTICES_DATA_POS_SIZE = 3
        const val VERTICES_DATA_UV_SIZE = 2
        const val VERTICES_DATA_STRIDE_BYTES =
            (VERTICES_DATA_POS_SIZE + VERTICES_DATA_UV_SIZE) * FLOAT_SIZE_BYTES
        const val VERTICES_DATA_POS_OFFSET = 0 * FLOAT_SIZE_BYTES
        const val VERTICES_DATA_UV_OFFSET =
            VERTICES_DATA_POS_OFFSET + VERTICES_DATA_POS_SIZE * FLOAT_SIZE_BYTES

        const val DEFAULT_UNIFORM_SAMPLER = "inputImageTexture"
    }

    var vertexShaderSource: String? = null
    var fragmentShaderSource: String? = null

    var programCurrent = 0

    private var vertexShader = 0
    private var fragmentShader = 0

    private var vertexBufferName = 0

    private val handleMap = HashMap<String, Int>()
    var originTexture = -1

    private var outputWidth = 0
    private var outputHeight = 0

    init {
        init()
    }

    private fun init() {
        this.vertexShaderSource = DEFAULT_VERTEX_SHADER
        this.fragmentShaderSource = DEFAULT_FRAGMENT_SHADER
    }

    constructor(vertexShaderSource: String?, fragmentShaderSource: String?) : this() {
        this.vertexShaderSource = vertexShaderSource
        this.fragmentShaderSource = fragmentShaderSource
    }

    /**
     * Init value for prepare draw
     */
    open fun setup() {
        release()
        vertexShader = EglUtil.loadShader(vertexShaderSource, GLES20.GL_VERTEX_SHADER)
        fragmentShader = EglUtil.loadShader(fragmentShaderSource, GLES20.GL_FRAGMENT_SHADER)
        programCurrent = EglUtil.createProgram(vertexShader, fragmentShader)
        vertexBufferName = EglUtil.createBuffer(VERTICES_DATA)
        getHandle("position")
        getHandle("inputTextureCoordinate")
        getHandle("inputImageTexture")
    }

    /**
     * Config frame size
     */
    open fun setFrameSize(width: Int, height: Int) {
        outputWidth = width
        outputHeight = height
    }

    /**
     * Release all object
     */
    open fun release() {
        if (programCurrent > 0) {
            GLES20.glDeleteProgram(programCurrent)
            programCurrent = 0
            GLES20.glDeleteShader(vertexShader)
            vertexShader = 0
            GLES20.glDeleteShader(fragmentShader)
            fragmentShader = 0
            GLES20.glDeleteBuffers(1, intArrayOf(vertexBufferName), 0)
            vertexBufferName = 0
            handleMap.clear()
        }
    }

    /**
     * Method call on draw to view
     */
    open fun draw(texName: Int, fbo: GLFramebufferObject?) {
        useProgram()
        onPreDraw()
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferName)
        GLES20.glEnableVertexAttribArray(getHandle("position"))
        GLES20.glVertexAttribPointer(
            getHandle("position"),
            VERTICES_DATA_POS_SIZE,
            GLES20.GL_FLOAT,
            false,
            VERTICES_DATA_STRIDE_BYTES,
            VERTICES_DATA_POS_OFFSET
        )
        GLES20.glEnableVertexAttribArray(getHandle("inputTextureCoordinate"))
        GLES20.glVertexAttribPointer(
            getHandle("inputTextureCoordinate"),
            VERTICES_DATA_UV_SIZE,
            GLES20.GL_FLOAT,
            false,
            VERTICES_DATA_STRIDE_BYTES,
            VERTICES_DATA_UV_OFFSET
        )
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texName)
        this@GLFilterObject.originTexture = texName
        GLES20.glUniform1i(getHandle("inputImageTexture"), 0)
        onDrawTexture(texName)
        onDraw()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(getHandle("position"))
        GLES20.glDisableVertexAttribArray(getHandle("inputTextureCoordinate"))
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        onFinishDraw()
    }

    /**
     * Method call predraw
     */
    open fun onPreDraw() {}

    /**
     * Draw gl here
     */
    open fun onDraw() {}

    /**
     * If you want change to draw and edit texture
     */
    open fun onDrawTexture(textureId: Int) {}

    /**
     * Draw finish
     */
    open fun onFinishDraw() {}

    /**
     * Method set program for using
     */
    protected fun useProgram() {
        GLES20.glUseProgram(programCurrent)
    }

    /**
     * Get vertexBufferName
     */
    protected fun getVertexBufferName(): Int {
        return vertexBufferName
    }

    /**
     * Get Handle by name
     */
    fun getHandle(name: String): Int {
        val value = handleMap[name]
        if (value != null) {
            return value
        }
        var location = GLES20.glGetAttribLocation(programCurrent, name)
        if (location == -1) {
            location = GLES20.glGetUniformLocation(programCurrent, name)
        }
        try {
            check(location != -1) { "Could not get attrib or uniform location for $name" }
        } catch (e: java.lang.Exception) {
            Timber.e(e)
            return -1
        }
        handleMap[name] = Integer.valueOf(location)
        return location
    }
}