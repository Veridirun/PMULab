package com.example.pmulab

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle

class NeptuneActivity : Activity() {

    private lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glSurfaceView = GLSurfaceView(this)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(NeptuneRenderer(this))
        setContentView(glSurfaceView)
    }
    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }
    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }
}
