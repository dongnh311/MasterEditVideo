package com.dongnh.masteredit.graph

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import com.dongnh.masteredit.gl.GLFilterObject
import java.nio.ByteBuffer

/**
 * Project : MasterEditVideo
 * Created by DongNH on 18/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class BlackWhiteForeground(private val context: Context) :
    GLFilterObject(BLACK_WHITE_VERTEXT_SHADER, BLACK_WHITE_FRAGMENT_SHADER) {

    companion object {
        val BLACK_WHITE_VERTEXT_SHADER = """
        attribute vec3 position;
        attribute vec2 inputTextureCoordinate;

        varying vec2 textureCoordinate;

        void main() {
            gl_Position = vec4(position, 1.0);
            textureCoordinate.xy = inputTextureCoordinate.xy;
        }
    """.trimIndent()

        const val BLACK_WHITE_FRAGMENT_SHADER = """
        precision highp float;
        varying vec2 textureCoordinate; 
        uniform sampler2D inputImageTexture; 
        uniform sampler2D inputImageTextureBlurred;
        uniform sampler2D inputImageTextureLookupFront;
        uniform float heightRatio;
        
        void main() {
            vec4 clearColor = clamp(texture2D(inputImageTexture, textureCoordinate), 0.0, 1.0);
            vec4 blurColor = clamp(texture2D(inputImageTextureBlurred, textureCoordinate.xy), 0.0, 1.0);
            //front filter
            highp float blueColor = clearColor.b * 63.0;
            highp vec2 quad1;
            quad1.y = floor(floor(blueColor) / 8.0);
            quad1.x = floor(blueColor) - (quad1.y * 8.0);
            highp vec2 texPos1;
            texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * clearColor.r);
            texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * clearColor.g);
            lowp vec4 newColorFront = texture2D(inputImageTextureLookupFront, texPos1);
        
            if(textureCoordinate.y > 0.5 - heightRatio / 2.0 && textureCoordinate.y < 0.5 + heightRatio / 2.0){
                gl_FragColor = newColorFront;
            }
            else{
                gl_FragColor = blurColor;
            }
        }
        """
    }

    var inputImageTexture = 0
    var inputImageTextureBlurred = 0
    var inputImageTextureLookupFront = 3
    var heightRatio = 0.31f
    var bitmap: Bitmap? = null
    private val textures = IntArray(1)
    var filterTextureId = -1
    private var adjustTextureId = -1

    init {
        this@BlackWhiteForeground.vertexShaderSource = BLACK_WHITE_VERTEXT_SHADER
        this@BlackWhiteForeground.fragmentShaderSource = BLACK_WHITE_FRAGMENT_SHADER
        initBitmapLUT()
    }

    /**
     * Init path of lut
     */
    private fun initBitmapLUT() {
        bitmap =
            BitmapFactory.decodeStream(context.assets.open("graph/resource/blackwhite/front.png"))

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

    // region draw on image

    // Draw in Image
    override fun setup() {
        super.setup()
        deleteTextureId()
        initTextureView()
    }

    // endregion

    // Draw video
    override fun onDraw() {

    }

    // Draw video
    override fun onDrawTexture(textureId: Int) {
        super.onDrawTexture(textureId)

        GLES20.glUniform1i(getHandle("inputImageTexture"), inputImageTexture)
        GLES20.glUniform1i(getHandle("inputImageTextureBlurred"), inputImageTextureBlurred)
        GLES20.glUniform1f(getHandle("heightRatio"), heightRatio)
        GLES20.glUniform1i(getHandle("inputImageTextureLookupFront"), inputImageTextureLookupFront)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, filterTextureId)
    }

    /**
     * Init texture for view
     */
    private fun initTextureView() {
        // Create view
        GLES20.glGenTextures(1, textures, 0)
        filterTextureId = textures[0]

        // Bind
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
        initBitmapLUT()

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    /**
     * Delete if need
     */
    private fun deleteTextureId() {
        if (filterTextureId != -1) {
            GLES20.glDeleteTextures(1, intArrayOf(filterTextureId), 0)
        }

        if (adjustTextureId != -1) {
            GLES20.glDeleteTextures(1, intArrayOf(adjustTextureId), 0)
        }
    }
}