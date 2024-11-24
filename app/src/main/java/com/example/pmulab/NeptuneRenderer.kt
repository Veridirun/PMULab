package com.example.pmulab

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.graphics.BitmapFactory
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.Matrix

class NeptuneRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private lateinit var sphereWater: SphereWater
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private lateinit var textureIds: IntArray
    private var textureId: Int = 0

    // Количество кадров
    private val frameCount = 16
    private val frameDuration = 100L // Длительность каждого кадра в миллисекундах

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        // Инициализация сферы воды
        sphereWater = SphereWater(radius = 2.0f).apply { initialize() }

        // Загрузка текстур для каждого кадра
        textureIds = IntArray(frameCount)
        val frameResources = intArrayOf(
            R.drawable.frame_001, R.drawable.frame_002, R.drawable.frame_003,
            R.drawable.frame_004, R.drawable.frame_005, R.drawable.frame_006,
            R.drawable.frame_007, R.drawable.frame_008, R.drawable.frame_009,
            R.drawable.frame_010, R.drawable.frame_011, R.drawable.frame_012,
            R.drawable.frame_013, R.drawable.frame_014, R.drawable.frame_015,
            R.drawable.frame_016
        )

        for (i in frameResources.indices) {
            textureIds[i] = loadTexture(context, frameResources[i])
        }
        textureId = loadTexture(context, R.drawable.water)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()

        // Установка проекционной матрицы
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 10f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Установка матрицы вида
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f)

        // Объединение проекционной и видовой матриц
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // Вычисление текущего кадра на основе времени
        val currentTime = System.currentTimeMillis()
        val currentFrame = ((currentTime / frameDuration) % frameCount).toInt()

        // Отрисовка сферы с текущей текстурой
        val time = (System.nanoTime() / 1000000000.0f) % 10
        sphereWater.draw(mvpMatrix, textureIds[currentFrame], time)
    }

    private fun loadTexture(context: Context, resourceId: Int): Int {
        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            val options = BitmapFactory.Options().apply { inScaled = false }
            val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

            // Загрузка текстуры
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle() // Освобождение ресурсов
        }

        return textureHandle[0]
    }
}
