package com.example.pmulab

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MoonRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var moon: SphereGL20
    private val mvpMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)

    private val lightPos = floatArrayOf(0.0f, 0.0f, 5.0f)  // Позиция источника света

    init {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.setIdentityM(projectionMatrix, 0)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        moon = SphereGL20(1.0f)

        Matrix.setLookAtM(
            viewMatrix, 0,
            0.0f, 0.0f, 5.0f,  // Позиция камеры
            0.0f, 0.0f, 0.0f,  // Направление взгляда
            0.0f, 1.0f, 0.0f   // Вектор "вверх"
        )
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Применение проекции и модельного преобразования
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)

        // Задаем цвет для луны
        val moonColor = floatArrayOf(0.8f, 0.8f, 0.8f, 1.0f)  // Серый цвет

        moon.loadTexture(context, R.drawable.moon)
        // Рисуем луну
        moon.draw(mvpMatrix, modelMatrix, lightPos, moonColor)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()

        // Устанавливаем перспективную матрицу проекции
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 10f)
    }
}
