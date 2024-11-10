package com.example.pmulab


import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity

class MoonActivity : ComponentActivity() {

    private lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glSurfaceView = GLSurfaceView(this).apply {
            setEGLContextClientVersion(2) // Используем OpenGL ES 2.0 для шейдеров
            setRenderer(MoonRenderer(this@MoonActivity))
        }
        setContentView(glSurfaceView)
    }
}