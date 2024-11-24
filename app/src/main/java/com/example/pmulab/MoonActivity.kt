package com.example.pmulab


import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.ComponentActivity

class MoonActivity : ComponentActivity() {

    private lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Создаем GLSurfaceView
        glSurfaceView = GLSurfaceView(this).apply {
            setEGLContextClientVersion(2) // Используем OpenGL ES 2.0 для шейдеров
            setRenderer(MoonRenderer(this@MoonActivity))
        }

        // Создаем TextView для отображения текста
        val textView = TextView(this).apply {
            text = "Информация о Луне: диаметр 3474 км, возраст около 4.5 млрд лет"
            textSize = 18f
            setTextColor(android.graphics.Color.WHITE)
            setPadding(16, 16, 16, 16)
        }

        // Используем FrameLayout для наложения элементов
        val frameLayout = FrameLayout(this).apply {
            addView(glSurfaceView) // Добавляем GLSurfaceView
            addView(textView)      // Добавляем TextView поверх
        }

        setContentView(frameLayout) // Устанавливаем макет активности
    }
}