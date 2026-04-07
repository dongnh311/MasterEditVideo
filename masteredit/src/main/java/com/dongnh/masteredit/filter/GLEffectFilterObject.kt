package com.dongnh.masteredit.filter

import android.content.Context
import com.dongnh.masteredit.gl.GLFilterObject
import com.dongnh.masteredit.model.SpecialModel
import timber.log.Timber

/**
 * Project : MasterEditVideo
 * GL filter that loads a GLSL fragment shader from assets and applies it as a video effect.
 */
class GLEffectFilterObject(context: Context, shaderPath: String) :
    GLFilterObject(VERTEX_SHADER_EFFECT, loadShaderFromAssets(context, shaderPath)) {

    companion object {
        // Vertex shader that maps UV to -1..1 range so existing effect fragment shaders
        // (which use textureCoordinate * 0.5 + 0.5) work correctly
        private val VERTEX_SHADER_EFFECT = """
            attribute highp vec3 position;
            attribute highp vec2 inputTextureCoordinate;
            varying highp vec2 textureCoordinate;
            void main() {
                gl_Position = vec4(position, 1.0);
                textureCoordinate = inputTextureCoordinate * 2.0 - 1.0;
            }
        """.trimIndent()

        fun loadShaderFromAssets(context: Context, path: String): String {
            return try {
                if (path.isEmpty()) {
                    DEFAULT_FRAGMENT_SHADER
                } else {
                    context.assets.open(path).bufferedReader().use { it.readText() }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load effect shader: $path")
                DEFAULT_FRAGMENT_SHADER
            }
        }
    }

    var specialModel: SpecialModel? = null
}
