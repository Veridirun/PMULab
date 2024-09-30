package com.example.pmulab

import android.R.attr
import android.R.attr.height
import android.R.attr.width
import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.opengl.GLU
import android.opengl.GLUtils
import android.util.DisplayMetrics
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class MyRenderer(context: Context, resources: Resources) : GLSurfaceView.Renderer {
    val textureName: IntArray = IntArray(1) { R.raw.background }
    val textures: IntArray = IntArray(1) { 0 }
    var c = context


    var cube: Cube
    var slab: Slab

    var displayMetrics: DisplayMetrics = context.resources.displayMetrics
    var dpHeight: Float = displayMetrics.heightPixels / displayMetrics.density
    var dpWidth: Float = displayMetrics.widthPixels / displayMetrics.density
    init{
        cube = Cube()
        slab = Slab()
    }
    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        gl.glGenTextures(1,textures,0)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR)

        val inputStream = c.resources.openRawResource(textureName[0])
        val bitmap = BitmapFactory.decodeStream(inputStream)
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D,0,bitmap,0)
        bitmap.recycle()
    }
    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        // Устанавливаем область отображения OpenGL на весь экран
        gl.glViewport(0, 0, width, height)

        // Соотношение сторон экрана
        val ratio: Float = width.toFloat() / height.toFloat()

        // Переходим в режим работы с матрицей проекции
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()

        // Настройка ортографической проекции
        val left = -ratio
        val right = ratio
        val bottom = -1.0f
        val top = 1.0f
        val near = 1.0f
        val far = 10.0f

        gl.glOrthof(left, right, bottom, top, near, far)

        // Переходим в режим работы с модельно-видовой матрицей
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
    }
    var num:Float = 0f
    var p:Float = 0f
    override fun onDrawFrame(gl: GL10) {
        gl.glClearColor(1f, 1f, 1f, 1f)
        gl.glClear(GL10.GL_DEPTH_BUFFER_BIT or GL10.GL_COLOR_BUFFER_BIT)
        gl.glLoadIdentity()

        gl.glTranslatef(0f,0f,-8f)

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D,textures[0]);
        gl.glScalef(1f,1f,1f)
        slab.draw(gl)
        gl.glDisable(GL10.GL_TEXTURE_2D)

        gl.glEnable(GL10.GL_DEPTH_TEST);
        //GLU.gluLookAt(gl,0f,1f,-2f,0f,2f,-1f,0f,1f,0f);
        //gl.glOrthof(-10f,10f, -5f,5f,5f,-5f);

        gl.glTranslatef(0f,0f,1f);
        gl.glRotatef(p,1f,1f,1f);
        gl.glScalef(1f,1f,1f)
        gl.glPushMatrix();
        p+=1f

        gl.glScalef(0.3f,0.3f,0.3f)
        cube.draw(gl);
        gl.glPopMatrix();
    }
}


