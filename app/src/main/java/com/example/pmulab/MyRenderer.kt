package com.example.pmulab

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class MyRenderer(context: Context, resources: Resources) : GLSurfaceView.Renderer {
    val textureName: IntArray = IntArray(1) { R.raw.background }
    val textures: IntArray = IntArray(1) { 0 }
    var c = context

    var a: FloatArray = floatArrayOf(
        -1f, 1f, 0f,
        -1f, -1f, 0f,
        1f, -1f, 0f,
        1f, 1f, 0f
    )
    val textureCoords: FloatArray = floatArrayOf(
        0f, 0f, // верхний левый угол
        0f, 1f, // нижний левый угол
        1f, 1f, // нижний правый угол
        1f, 0f  // верхний правый угол
    )

    var f: FloatBuffer
    var b: ByteBuffer
    var texBuffer: FloatBuffer


    init{
        //буфер координат вершин
        b=ByteBuffer.allocateDirect(4*3*4);
        b.order(ByteOrder.nativeOrder());
        f=b.asFloatBuffer();
        f.put(a);
        f.position(0);

        //буфер текстурных координат
        val byteBuffer = ByteBuffer.allocateDirect(textureCoords.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        texBuffer = byteBuffer.asFloatBuffer()
        texBuffer.put(textureCoords)
        texBuffer.position(0)
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
    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {}
    override fun onDrawFrame(gl: GL10) {
        gl.glClearColor(1f, 1f, 1f, 1f)
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
        gl.glLoadIdentity();
        gl.glTranslatef(0f,0f,-1f)
        //gl.glScalef(0.5f,0.5f,0.5f)
        //gl.glColor4f(0f,1f,1f,1f)//color of rectangle
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, f) //3 coords per vertex

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D,textures[0]);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glTexCoordPointer(2,GL10.GL_FLOAT,0,texBuffer);


        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4) //draw rectangle
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisable(GL10.GL_TEXTURE_2D)
    }

}


