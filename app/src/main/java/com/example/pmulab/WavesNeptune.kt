package com.example.pmulab

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.cos
import kotlin.math.sin

class ShaderProgram(vertexShaderCode: String, fragmentShaderCode: String) {
    var programId: Int

    init {
        val vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        programId = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0)
    }

    fun use() {
        GLES20.glUseProgram(programId)
    }

    private fun compileShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

        return shader
    }

    init {
        val vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        programId = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)

            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0)
        }
    }

}

class SphereWater(
    private val latitudeBands: Int = 40,
    private val longitudeBands: Int = 40,
    val radius: Float = 1.0f
) {
    private lateinit var shaderProgram: ShaderProgram
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var indexBuffer: ShortBuffer
    private lateinit var textureBuffer: FloatBuffer

    private val vertices: FloatArray
    private val indices: ShortArray
    private val textureCoords: FloatArray

    init {
        val vertexCount = (latitudeBands + 1) * (longitudeBands + 1)
        vertices = FloatArray(vertexCount * 3)
        textureCoords = FloatArray(vertexCount * 2)
        val indexCount = latitudeBands * longitudeBands * 6
        indices = ShortArray(indexCount)

        var vertexIndex = 0
        var indexIndex = 0

        for (lat in 0..latitudeBands) {
            val theta = lat * Math.PI / latitudeBands
            val sinTheta = sin(theta).toFloat()
            val cosTheta = cos(theta).toFloat()

            for (long in 0..longitudeBands) {
                val phi = long * 2 * Math.PI / longitudeBands
                val sinPhi = sin(phi).toFloat()
                val cosPhi = cos(phi).toFloat()

                // Определяем вершины
                vertices[vertexIndex++] = cosPhi * sinTheta * radius
                vertices[vertexIndex++] = cosTheta * radius
                vertices[vertexIndex++] = sinPhi * sinTheta * radius

                // Определяем текстурные координаты
                textureCoords[(lat * (longitudeBands + 1) + long) * 2] = 1f - (long / longitudeBands.toFloat())
                textureCoords[(lat * (longitudeBands + 1) + long) * 2 + 1] = 1f - (lat / latitudeBands.toFloat())

                // Генерируем индексы
                if (lat < latitudeBands && long < longitudeBands) {
                    val first = (lat * (longitudeBands + 1) + long).toShort()
                    val second = (first + longitudeBands + 1).toShort()

                    // Первый треугольник
                    indices[indexIndex++] = first
                    indices[indexIndex++] = second
                    indices[indexIndex++] = (first + 1).toShort()

                    // Второй треугольник
                    indices[indexIndex++] = second
                    indices[indexIndex++] = (second + 1).toShort()
                    indices[indexIndex++] = (first + 1).toShort()
                }
            }
        }
    }

    fun initialize() {
        shaderProgram = ShaderProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE)

        vertexBuffer = createFloatBuffer(vertices)
        indexBuffer = createShortBuffer(indices)
        textureBuffer = createFloatBuffer(textureCoords)
    }

    private fun createFloatBuffer(data: FloatArray): FloatBuffer {
        return ByteBuffer.allocateDirect(data.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(data)
                position(0)
            }
        }
    }

    private fun createShortBuffer(data: ShortArray): ShortBuffer {
        return ByteBuffer.allocateDirect(data.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(data)
                position(0)
            }
        }
    }

    fun draw(mvpMatrix: FloatArray, textureId: Int, time: Float) {
        shaderProgram.use()

        val positionHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "a_Position")
        val texCoordHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "a_TexCoord")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "u_MVPMatrix")
        val timeHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "u_Time")

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glUniform1f(timeHandle, time)

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glEnableVertexAttribArray(texCoordHandle)

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }

    companion object {
        private const val VERTEX_SHADER_CODE = """
            attribute vec4 a_Position;
            attribute vec2 a_TexCoord;
            uniform mat4 u_MVPMatrix;
            uniform float u_Time;
            varying vec2 v_TexCoord;

            void main() {
                float waveHeightX = sin(a_Position.x * 10.0 + u_Time) * 0.05; 
                float waveHeightZ = sin(a_Position.z * 10.0 + u_Time) * 0.05; 
                float waveHeight = waveHeightX + waveHeightZ;

                vec4 modPosition = a_Position;
                modPosition.y += waveHeight;

                gl_Position = u_MVPMatrix * modPosition;
                v_TexCoord = a_TexCoord;
            }
        """

        private const val FRAGMENT_SHADER_CODE = """
            precision mediump float;
            varying vec2 v_TexCoord;
            uniform sampler2D u_Texture;

            void main() {
                vec4 texColor = texture2D(u_Texture, v_TexCoord);
                gl_FragColor = texColor;
            }
        """
    }
}
