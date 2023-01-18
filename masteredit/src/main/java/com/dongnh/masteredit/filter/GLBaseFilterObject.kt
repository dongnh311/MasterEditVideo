package com.dongnh.masteredit.filter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import com.dongnh.masteredit.gl.GLFilterObject
import com.dongnh.masteredit.model.SpecialModel
import java.nio.ByteBuffer

/**
 * Project : MasterEditVideo
 * Created by DongNH on 16/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class GLBaseFilterObject(private val context: Context, private var pathImage: String) :
    GLFilterObject(VERTEX_SHADER_LUT, FRAGMENT_SHADER_LUT) {

    companion object {
        val VERTEX_SHADER_LUT = "precision highp float;" +
                "attribute vec4 position; " +
                "attribute vec4 inputTextureCoordinate;" +
                "varying vec2 textureCoordinate;" +
                "void main() {" +
                "    gl_Position = position; " +
                "    textureCoordinate = inputTextureCoordinate.xy; " +
                "}"

        val FRAGMENT_SHADER_LUT = "precision highp float;" +
                "varying highp vec2 textureCoordinate;" +
                "varying highp vec2 textureCoordinate2;" +
                "uniform sampler2D inputImageTexture;" +
                "uniform sampler2D inputImageTextureLookup;" +
                "uniform float intensity;" +
                "void main () {" +
                " vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);" +
                " if (textureCoordinate.y < 1.0) {" +
                "     float yColor = textureColor.b * 63.0;" +
                "     vec2 quad1;" +
                "     quad1.y = floor(floor(yColor) / 8.0);" +
                "     quad1.x = floor(yColor) - (quad1.y * 8.0);" +
                "     vec2 quad2;" +
                "     quad2.y = floor(ceil(yColor) / 8.0);" +
                "     quad2.x = ceil(yColor) - (quad2.y * 8.0);" +
                "     vec2 texPos1;" +
                "     texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);" +
                "     texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);" +
                "     vec2 texPos2;" +
                "     texPos2.x = (quad2.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);" +
                "     texPos2.y = (quad2.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);" +
                "     vec4 newColor1;" +
                "     vec4 newColor2;" +
                "     newColor1 = texture2D(inputImageTextureLookup, texPos1);" +
                "     newColor2 = texture2D(inputImageTextureLookup, texPos2);" +
                "     vec4 newColor = mix(newColor1, newColor2, fract(yColor));" +
                "     gl_FragColor = mix(textureColor, vec4(newColor.rgb, textureColor.a), intensity);" +
                " } else {" +
                "     gl_FragColor = textureColor;" +
                " }" +
                "}"
    }

    private var inputImageTexture = 0
    var intensity = 0.7f
    private var bitmap: Bitmap? = null
    private val textures = IntArray(1)
    private var filterTextureId = -1
    var specialModel: SpecialModel? = null

    init {
        this@GLBaseFilterObject.vertexShaderSource = VERTEX_SHADER_LUT
        this@GLBaseFilterObject.fragmentShaderSource = FRAGMENT_SHADER_LUT
        initBitmapLUT(pathImage)
    }

    /**
     * Init path of lut
     */
    private fun initBitmapLUT(pathImage: String) {
        this@GLBaseFilterObject.pathImage = pathImage
        bitmap =
            BitmapFactory.decodeStream(context.assets.open(pathImage))

        if (bitmap != null) {
            if (filterTextureId != -1) {

                // Calculate how many bytes our image consists of.
                val bytes: Int = bitmap!!.byteCount
                val buffer: ByteBuffer = ByteBuffer.allocateDirect(bytes) // Create a new buffer

                bitmap!!.copyPixelsToBuffer(buffer) // Move the byte data to the buffer
                buffer.flip()
                buffer.position(0)

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, filterTextureId)
                GLES20.glTexImage2D(
                    GLES20.GL_TEXTURE_2D,
                    0,
                    GLES20.GL_RGBA,
                    512,
                    512,
                    0,
                    GLES20.GL_RGBA,
                    GLES20.GL_UNSIGNED_BYTE,
                    buffer
                )
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
            }
        }
    }

    // For video
    override fun onPreDraw() {
        super.onPreDraw()
        deleteTextureId()
        initTextureView()
    }

    // Edit video draw here
    override fun onDraw() {
        GLES20.glUniform1i(getHandle("inputImageTexture"), inputImageTexture)
        GLES20.glUniform1i(getHandle("inputImageTextureLookup"), 3)
        GLES20.glUniform1f(getHandle("intensity"), intensity)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, filterTextureId)
    }

    /**
     * Init texture for view
     */
    private fun initTextureView() {
        GLES20.glGenTextures(1, textures, 0)
        filterTextureId = textures[0]
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, filterTextureId)

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
        if (pathImage.isNotEmpty()) {
            initBitmapLUT(pathImage)
        } else {
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_RGBA,
                512,
                512,
                0,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                null
            )
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    /**
     * Delete if need
     */
    private fun deleteTextureId() {
        if (filterTextureId != -1) {
            GLES20.glDeleteTextures(1, intArrayOf(filterTextureId), 0)
        }
    }
}