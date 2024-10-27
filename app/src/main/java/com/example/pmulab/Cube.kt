package com.example.pmulab

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10




class Cube {
    private var mVertexBuffer: FloatBuffer
    private var mColorBuffer: FloatBuffer
    private var mIndexBuffer: ByteBuffer

    private val vertices = floatArrayOf(
        -1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f, 1.0f, -1.0f,
        -1.0f, 1.0f, -1.0f,
        -1.0f, -1.0f, 1.0f,
        1.0f, -1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        -1.0f, 1.0f, 1.0f
    )
    private val color: FloatArray = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f) // Цвет по умолчанию: белый
//    private val colors = floatArrayOf(
//        0.0f, 1.0f, 0.0f, 1.0f,
//        0.0f, 1.0f, 0.0f, 1.0f,
//        1.0f, 0.5f, 0.0f, 1.0f,
//        1.0f, 0.5f, 0.0f, 1.0f,
//        1.0f, 0.0f, 1.0f, 1.0f,
//        1.0f, 0.0f, 0.0f, 1.0f,
//        1.0f, 0.0f, 0.0f, 1.0f,
//        0.0f, 0.0f, 1.0f, 1.0f,  //1.0f,  0.0f,  1.0f,  1.0f
//    )

    private val indices = byteArrayOf(
        0, 4, 5, 0, 5, 1,
        1, 5, 6, 1, 6, 2,
        2, 6, 7, 2, 7, 3,
        3, 7, 4, 3, 4, 0,
        4, 7, 6, 4, 6, 5,
        3, 0, 1, 3, 1, 2
    )
    init{
        var byteBuf = ByteBuffer.allocateDirect(vertices.size * 4)
        byteBuf.order(ByteOrder.nativeOrder())
        mVertexBuffer = byteBuf.asFloatBuffer()
        mVertexBuffer.put(vertices)
        mVertexBuffer.position(0)

        byteBuf = ByteBuffer.allocateDirect(vertices.size/3 * 4 * 4)
        byteBuf.order(ByteOrder.nativeOrder())
        mColorBuffer = byteBuf.asFloatBuffer()
        //mColorBuffer.put(colors)
        //mColorBuffer.position(0)

        mIndexBuffer = ByteBuffer.allocateDirect(indices.size)
        mIndexBuffer.put(indices)
        mIndexBuffer.position(0)
    }

    fun draw(gl: GL10, alpha: Float) {
        //gl.glFrontFace(GL10.GL_CW);
        // gl.glEnable(GL10.GL_BACK);
        // Устанавливаем цвет с учётом переданного параметра прозрачности (alpha)
        val colorArray = FloatArray(vertices.size / 3 * 4) // Один и тот же цвет для всех 8 вершин
        for (i in colorArray.indices step 4) {
            colorArray[i] = color[0]   // R
            colorArray[i + 1] = color[1] // G
            colorArray[i + 2] = color[2] // B
            colorArray[i + 3] = alpha    // Alpha
        }
        mColorBuffer.clear()
        mColorBuffer.put(colorArray)
        mColorBuffer.position(0)

        gl.glEnable(GL10.GL_BLEND)
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer)
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer)

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY)

        gl.glDrawElements(
            GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE,
            mIndexBuffer
        )

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY)
        gl.glDisable(GL10.GL_BLEND)
        // gl.glDisable(GL10.GL_CULL_FACE);
    }

}