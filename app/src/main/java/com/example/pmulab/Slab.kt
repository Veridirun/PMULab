package com.example.pmulab

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10


class Slab {
    //private int texturenum;
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
        /*texturenum=com.namespace.galery.TriangleRenderer.textures_count;
			  com.namespace.galery.TriangleRenderer.textures_count++;
			  com.namespace.galery.TriangleRenderer.texture_name[ texturenum]=com.namespace.galery.R.drawable.wall;
			  */
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

    fun draw(gl: GL10) {
        //gl.glEnable(GL10.GL_CULL_FACE);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer)


        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4)


        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisable(GL10.GL_CULL_FACE)
    }
}