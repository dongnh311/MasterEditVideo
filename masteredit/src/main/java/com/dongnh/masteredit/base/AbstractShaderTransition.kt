package com.dongnh.masteredit.base

import android.content.Context
import android.content.res.AssetManager
import android.opengl.GLES20
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Project : MasterEditVideo
 * Created by DongNH on 16/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
abstract class AbstractShaderTransition {

    companion object {
        const val SHADER_FOLDER = "effect/resource/shaders/"

        const val TRANSITION_FOLDER = "transition/resource/"
    }

    private var shaderId = -1

    private val locationMap: MutableMap<String, Location> = HashMap()

    open fun initShader(
        shaderSource: String,
        type: Int,
        context: Context
    ): Boolean {
        return initShader(arrayOf(SHADER_FOLDER + shaderSource), type, context)
    }

    protected open fun initShader(
        shaderSourceList: Array<String>,
        type: Int,
        context: Context
    ): Boolean {
        val compiled = IntArray(1)
        shaderId = GLES20.glCreateShader(type)
        val loadShader = loadShader(shaderSourceList, context)
        GLES20.glShaderSource(shaderId, loadShader)
        GLES20.glCompileShader(shaderId)
        GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            shaderId = -1
            Timber.e(
                "Load Shader Failed Compilation ${GLES20.glGetShaderInfoLog(shaderId)}"
            )
            return false
        }
        return true
    }

    protected fun addLocation(name: String, isUniform: Boolean) {
        locationMap[name] = Location(isUniform)
    }

    protected open fun loadLocation(programId: Int) {
        val nameSet: Set<String> = locationMap.keys
        for (name in nameSet) {
            val location = locationMap[name]
            if (location!!.isUniform) {
                location.location = GLES20.glGetUniformLocation(programId, name)
            } else {
                location.location = GLES20.glGetAttribLocation(programId, name)
            }
        }
    }

    protected fun setUniform(name: String, value: Float) {
        GLES20.glUniform1f(locationMap[name]!!.location, value)
    }

    protected fun setUniform(name: String, value: Int) {
        GLES20.glUniform1i(locationMap[name]!!.location, value)
    }

    protected fun setUniform(name: String, value: Boolean) {
        GLES20.glUniform1i(locationMap[name]!!.location, if (value) 1 else 0)
    }

    protected fun setUniform(name: String, v1: Int, v2: Int) {
        val values = IntArray(2)
        values[0] = v1
        values[1] = v2
        GLES20.glUniform2iv(locationMap[name]!!.location, 1, values, 0)
    }

    protected fun setUniform(name: String, v1: Float, v2: Float) {
        val values = FloatArray(2)
        values[0] = v1
        values[1] = v2
        GLES20.glUniform2fv(locationMap[name]!!.location, 1, values, 0)
    }

    protected fun setUniform(name: String, v1: Float, v2: Float, v3: Float) {
        val values = FloatArray(3)
        values[0] = v1
        values[1] = v2
        values[2] = v3
        GLES20.glUniform3fv(locationMap[name]!!.location, 1, values, 0)
    }

    protected fun setUniform(name: String, v1: Float, v2: Float, v3: Float, v4: Float) {
        val values = FloatArray(4)
        values[0] = v1
        values[1] = v2
        values[2] = v3
        values[3] = v4
        GLES20.glUniform4fv(locationMap[name]!!.location, 1, values, 0)
    }

    open fun getShaderId(): Int {
        return shaderId
    }

    abstract fun initLocation(programId: Int)

    open fun loadShader(
        assetPathList: Array<String>,
        context: Context
    ): String? {
        var result: String? = null
        var inputStream: InputStream? = null
        var bufferedReader: BufferedReader? = null
        val stringBuilder = StringBuilder()
        try {
            for (assetPath in assetPathList) {
                val am: AssetManager = context.assets!!
                inputStream = am.open(assetPath)
                val inputStreamReader = InputStreamReader(inputStream)
                bufferedReader = BufferedReader(inputStreamReader)
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                    stringBuilder.append("\n")
                }
            }
            result = stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                bufferedReader?.close()
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return result
    }

    private class Location(var isUniform: Boolean) {
        var location = -1
    }
}