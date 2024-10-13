package com.example.pmulab

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10


class Slab {
    private var textureID: Int = 0
    private val vertexBuffer: FloatBuffer // Buffer for vertex-array
    private val texBuffer: FloatBuffer // Buffer for texture-coords-array (NEW)

    private val vertices = floatArrayOf( // Vertices for a face
        -1.0f, -1.0f, 0.0f,  // 0. left-bottom-front
        1.0f, -1.0f, 0.0f,  // 1. right-bottom-front
        -1.0f, 1.0f, 0.0f,  // 2. left-top-front
        1.0f, 1.0f, 0.0f // 3. right-top-front
    )

    var texCoords: FloatArray = floatArrayOf( // Texture coords for the above face (NEW)
        0.0f, 1.0f,  // A. left-bottom (NEW)
        1.0f, 1.0f,  // B. right-bottom (NEW)
        0.0f, 0.0f,  // C. left-top (NEW)
        1.0f, 0.0f // D. right-top (NEW)
    )
    var textureIDs: IntArray = IntArray(1) // Array for 1 texture-ID (NEW)

    // Constructor - Set up the buffers
    init {
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder()) // Use native byte order
        vertexBuffer = vbb.asFloatBuffer() // Convert from byte to float
        vertexBuffer.put(vertices) // Copy data into buffer
        vertexBuffer.position(0) // Rewind


        // Setup texture-coords-array buffer, in float. An float has 4 bytes (NEW)
        val tbb = ByteBuffer.allocateDirect(texCoords.size * 4)
        tbb.order(ByteOrder.nativeOrder())
        texBuffer = tbb.asFloatBuffer()
        texBuffer.put(texCoords)
        texBuffer.position(0)
    }

    fun loadTexture(gl: GL10, context: Context, resourceId: Int) {
//        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
//        val textures = IntArray(1)
//
//        gl.glGenTextures(1, textures, 0)
//        textureID = textures[0]
//
//        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID)
//        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
//        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat())
//        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
//
//        bitmap.recycle()
        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)

        val textures = IntArray(1)
        gl.glGenTextures(1, textures, 0)
        textureID = textures[0]

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID)
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT.toFloat())

        bitmap.recycle()
    }

    fun draw(gl: GL10) {
        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID)

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer)

        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4)

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glDisable(GL10.GL_TEXTURE_2D)
    }
}