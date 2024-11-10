package com.example.pmulab

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin


class Sphere(private val radius: Float) {
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer
    private lateinit var indexBuffer: ShortBuffer
    private var textureId = 0

    init {
        initializeBuffers()
    }

    private fun initializeBuffers() {
        val numSlices = 36
        val numStacks = 18

        val numVertices = (numSlices + 1) * (numStacks + 1)
        val numIndices = numSlices * numStacks * 6

        val vertices = FloatArray(numVertices * 3)
        val textureCoords = FloatArray(numVertices * 2)
        val indices = ShortArray(numIndices)

        val stackStep = Math.PI.toFloat() / numStacks
        val sliceStep = (2 * Math.PI).toFloat() / numSlices

        var vertexIndex = 0
        var texCoordIndex = 0
        for (i in 0..numStacks) {
            val stackAngle = (Math.PI / 2 - i * stackStep).toFloat()
            val xy = cos(stackAngle.toDouble()).toFloat()
            val z = sin(stackAngle.toDouble()).toFloat()

            for (j in 0..numSlices) {
                val sliceAngle = j * sliceStep
                val x = xy * cos(sliceAngle.toDouble()).toFloat()
                val y = xy * sin(sliceAngle.toDouble()).toFloat()

                vertices[vertexIndex++] = x * radius
                vertices[vertexIndex++] = y * radius
                vertices[vertexIndex++] = z * radius

                textureCoords[texCoordIndex++] = j.toFloat() / numSlices
                textureCoords[texCoordIndex++] = 1 - i.toFloat() / numStacks
            }
        }

        var index = 0
        for (i in 0 until numStacks) {
            for (j in 0 until numSlices) {
                val v1 = (i * (numSlices + 1) + j).toShort()
                val v2 = (i * (numSlices + 1) + (j + 1)).toShort()
                val v3 = ((i + 1) * (numSlices + 1) + j).toShort()
                val v4 = ((i + 1) * (numSlices + 1) + (j + 1)).toShort()

                indices[index++] = v1
                indices[index++] = v2
                indices[index++] = v3
                indices[index++] = v2
                indices[index++] = v4
                indices[index++] = v3
            }
        }

        var byteBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        vertexBuffer = byteBuffer.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        byteBuffer = ByteBuffer.allocateDirect(textureCoords.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        textureBuffer = byteBuffer.asFloatBuffer()
        textureBuffer.put(textureCoords)
        textureBuffer.position(0)

        byteBuffer = ByteBuffer.allocateDirect(indices.size * 2)
        byteBuffer.order(ByteOrder.nativeOrder())
        indexBuffer = byteBuffer.asShortBuffer()
        indexBuffer.put(indices)
        indexBuffer.position(0)
    }

    fun loadTexture(gl: GL10, context: Context, resourceId: Int) {
        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)

        val textures = IntArray(1)
        gl.glGenTextures(1, textures, 0)
        textureId = textures[0]

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId)
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT.toFloat())

        bitmap.recycle()
    }

    fun draw(gl: GL10) {
        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId)

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer)

        gl.glDrawElements(
            GL10.GL_TRIANGLES,
            indexBuffer.capacity(),
            GL10.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glDisable(GL10.GL_TEXTURE_2D)
    }

}