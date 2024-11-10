package com.example.pmulab

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLUtils
import android.graphics.BitmapFactory
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

class SphereGL20(private val radius: Float, private val stacks: Int = 18, private val slices: Int = 36) {
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var normalBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer
    private lateinit var indexBuffer: ByteBuffer
    private var shaderProgram: Int = 0
    private var textureId: Int = 0
    private var numIndices: Int = 0

    init {
        initBuffers()
        initShaderProgram()
    }

    private fun initBuffers() {
        val vertices = mutableListOf<Float>()
        val normals = mutableListOf<Float>()
        val textureCoords = mutableListOf<Float>()
        val indices = mutableListOf<Byte>()

        val stackStep = Math.PI.toFloat() / stacks
        val sliceStep = (2 * Math.PI).toFloat() / slices

        for (i in 0..stacks) {
            val stackAngle = (Math.PI / 2 - i * stackStep).toFloat()
            val xy = radius * cos(stackAngle.toDouble()).toFloat()
            val z = radius * sin(stackAngle.toDouble()).toFloat()

            for (j in 0..slices) {
                val sliceAngle = j * sliceStep
                val x = xy * cos(sliceAngle.toDouble()).toFloat()
                val y = xy * sin(sliceAngle.toDouble()).toFloat()

                // Vertex
                vertices.add(x)
                vertices.add(y)
                vertices.add(z)

                // Normal
                normals.add(x / radius)
                normals.add(y / radius)
                normals.add(z / radius)

                // Texture coordinates
                textureCoords.add(j.toFloat() / slices)
                textureCoords.add(i.toFloat() / stacks)
            }
        }

        for (i in 0 until stacks) {
            for (j in 0 until slices) {
                val first = i * (slices + 1) + j
                val second = first + slices + 1

                indices.add(first.toByte())
                indices.add(second.toByte())
                indices.add((first + 1).toByte())

                indices.add(second.toByte())
                indices.add((second + 1).toByte())
                indices.add((first + 1).toByte())
            }
        }
        numIndices = indices.size

        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(vertices.toFloatArray())
            position(0)
        }
        normalBuffer = ByteBuffer.allocateDirect(normals.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(normals.toFloatArray())
            position(0)
        }
        textureBuffer = ByteBuffer.allocateDirect(textureCoords.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(textureCoords.toFloatArray())
            position(0)
        }
        indexBuffer = ByteBuffer.allocateDirect(indices.size).order(ByteOrder.nativeOrder()).apply {
            put(indices.toByteArray())
            position(0)
        }
    }

    private fun initShaderProgram() {
        val vertexShaderCode = """
            attribute vec4 aPosition;
            attribute vec3 aNormal;
            attribute vec2 aTexCoord;
            uniform mat4 uMVPMatrix;
            uniform mat4 uModelMatrix;
            varying vec3 vNormal;
            varying vec3 vPosition;
            varying vec2 vTexCoord;

            void main() {
                vPosition = vec3(uModelMatrix * aPosition);
                vNormal = vec3(uModelMatrix * vec4(aNormal, 0.0));
                vTexCoord = aTexCoord;
                gl_Position = uMVPMatrix * aPosition;
            }
        """

        val fragmentShaderCode = """
            precision mediump float;
            uniform vec3 uLightPos;  // Позиция источника света
            uniform vec4 uColor;      // Основной цвет объекта (например, для амбиентного освещения)
            uniform sampler2D uTexture; // Текстура объекта
            
            varying vec3 vNormal;   // Нормаль для пикселя
            varying vec3 vPosition; // Позиция пикселя в мировых координатах
            varying vec2 vTexCoord; // Текстурные координаты
            
            void main() {
                // Направление на источник света
                vec3 lightDir = normalize(uLightPos - vPosition);
                
                // Диффузное освещение (освещенность, пропорциональная углу между нормалью и направлением на свет)
                float diff = max(dot(vNormal, lightDir), 0.0);  // Максимум из скалярного произведения (норма нормали)
                vec4 diffuse = diff * uColor; // Диффузное освещение с умножением на основной цвет объекта
            
                // Получаем цвет из текстуры
                vec4 textureColor = texture2D(uTexture, vTexCoord);
            
                // Итоговый цвет: освещенность по модели Фонга + текстура
                gl_FragColor = diffuse * textureColor + 0.1 * uColor; // Добавление амбиентного света (0.1 * uColor)
            }
        """

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        shaderProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    fun loadTexture(context: Context, resourceId: Int) {
        val textureData = BitmapFactory.decodeResource(context.resources, resourceId)
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        textureId = textures[0]

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textureData, 0)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        textureData.recycle()
    }

    fun draw(mvpMatrix: FloatArray, modelMatrix: FloatArray, lightPos: FloatArray, color: FloatArray) {
        GLES20.glUseProgram(shaderProgram)

        val positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition")
        val normalHandle = GLES20.glGetAttribLocation(shaderProgram, "aNormal")
        val texCoordHandle = GLES20.glGetAttribLocation(shaderProgram, "aTexCoord")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix")
        val modelMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uModelMatrix")
        val lightPosHandle = GLES20.glGetUniformLocation(shaderProgram, "uLightPos")
        val colorHandle = GLES20.glGetUniformLocation(shaderProgram, "uColor")
        val textureHandle = GLES20.glGetUniformLocation(shaderProgram, "uTexture")

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glEnableVertexAttribArray(normalHandle)
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, normalBuffer)

        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(modelMatrixHandle, 1, false, modelMatrix, 0)
        GLES20.glUniform3fv(lightPosHandle, 1, lightPos, 0)
        GLES20.glUniform4fv(colorHandle, 1, color, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureHandle, 0)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, numIndices, GLES20.GL_UNSIGNED_BYTE, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(normalHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }
}
