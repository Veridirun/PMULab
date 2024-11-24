package com.example.pmulab

import android.content.Context
import android.content.res.Resources
import android.opengl.GLSurfaceView
import android.opengl.GLU
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.abs
import kotlin.math.sin


class MyRenderer(context: Context) : GLSurfaceView.Renderer {
    var context = context

    lateinit var cube: Cube

    lateinit var background: Slab

    lateinit var sun: Sphere
    lateinit var earth: Sphere
    lateinit var moon: Sphere
    lateinit var venus: Sphere
    lateinit var mars: Sphere
    lateinit var jupiter: Sphere
    lateinit var saturn: Sphere
    lateinit var mercury: Sphere
    lateinit var uranus: Sphere
    lateinit var neptune: Sphere
    lateinit var blackHole: Sphere
    private var blackHolePosition = floatArrayOf(-20.0f, 0.0f, 0.0f) // Начальная позиция

    private var angleSun = 0.0f
    private var angleEarth = 0.0f
    private var angleMoon = 0.0f
    private var angleEarthOrbit = 0.0f
    private var angleVenusOrbit = 0.0f
    private var angleMarsOrbit = 0.0f
    private var angleJupiterOrbit = 0.0f
    private var angleSaturnOrbit = 0.0f
    private var angleMercuryOrbit = 0.0f
    private var angleUranusOrbit = 0.0f
    private var angleNeptuneOrbit = 0.0f
    private var blackHoleSpeed = 0.1f
    private var alphaAngle : Float = 0.0f
    private var alpha : Float = 0.0f

    private var selectedPlanet = 0;

    fun getPlanet(): Int {
        return selectedPlanet
    }

    fun previousPlanet() {
        selectedPlanet--;
        if(selectedPlanet<0){
            selectedPlanet = 10; //11-я планета
        }
        Log.w("PMUrenderer", "planet = $selectedPlanet");
    }

    fun nextPlanet() {
        selectedPlanet++;
        if(selectedPlanet>10){
            selectedPlanet = 0; //1-я планета
        }
        Log.w("PMUrenderer", "planet = $selectedPlanet");
    }

    init{
        background = Slab()
        cube = Cube()
        sun = Sphere(1f)
        earth = Sphere(0.25f)
        moon = Sphere(0.05f)
        venus = Sphere(0.25f)
        mars = Sphere(0.2f)
        jupiter = Sphere(0.5f)
        saturn = Sphere(0.6f)
        mercury = Sphere(0.15f)
        uranus = Sphere(0.3f)
        neptune = Sphere(0.3f)
        blackHole = Sphere(0.4f) // Радиус черной дыры
    }
    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        background.loadTexture(gl, context, R.drawable.background)
        sun.loadTexture(gl, context, R.drawable.sun)
        earth.loadTexture(gl, context, R.drawable.earth)
        moon.loadTexture(gl, context, R.drawable.moon)
        venus.loadTexture(gl, context, R.drawable.venus)
        mars.loadTexture(gl, context, R.drawable.mars)
        jupiter.loadTexture(gl, context, R.drawable.jupiter)
        saturn.loadTexture(gl, context, R.drawable.saturn)
        mercury.loadTexture(gl, context, R.drawable.mercury)
        uranus.loadTexture(gl, context, R.drawable.uranus)
        neptune.loadTexture(gl, context, R.drawable.neptune)
        blackHole.loadTexture(gl, context, R.drawable.black_hole)

    }
    var aspectRatio: Float = 1.0f
    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        gl.glViewport(0, 0, width, height)
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()

        aspectRatio = width.toFloat() / height
        GLU.gluPerspective(gl, 60.0f, aspectRatio, 1.0f, 100.0f)

        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
    }
    // Функция для отрисовки фона
    private fun drawBackground(gl: GL10) {
        // Переключаемся в режим орфографической проекции
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glPushMatrix()                        // Сохраняем текущую проекцию
        gl.glLoadIdentity()                      // Сбрасываем матрицу
        gl.glOrthof(-1.0f, 1.0f, -1.5f, 1.5f, 1.0f, 100.0f)  // Устанавливаем орфографическую проекцию

        // Очищаем ModelView и рисуем фон
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()

        // Рисуем фон с масштабированием
        gl.glPushMatrix()
        gl.glTranslatef(0.0f, 0.0f, -1.0f)      // Размещаем фон позади камеры
        gl.glScalef(1.5f, 1.5f, 1.0f)           // Масштабируем фон, чтобы он занимал весь экран
        background.draw(gl)
        gl.glPopMatrix()

        // Восстанавливаем прежнюю проекцию
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glPopMatrix()
    }

    override fun onDrawFrame(gl: GL10) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

        // Отрисовка фона
        drawBackground(gl)

        // === Переход к перспективной проекции для отрисовки объектов ===
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()

        GLU.gluPerspective(gl, 60.0f, aspectRatio, 1.0f, 100.0f)

        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()

        // Настройка камеры под углом
        GLU.gluLookAt(gl, 20.0f, 40.0f, 00.0f,   // Позиция камеры
            0.0f, 0.0f, 0.0f,          // Камера смотрит на Солнце
            0.0f, 0.0f, 1.0f)


        gl.glPushMatrix()
        gl.glTranslatef(0.0f, 0.0f, 0.0f) // Позиция солнца
        // Отрисовка Солнца
        gl.glPushMatrix()
        gl.glRotatef(angleSun, 0.0f, 1.0f, 0.0f) // Вращение вокруг своей оси
        sun.draw(gl)
        if(selectedPlanet == 0) cube.draw(gl, alpha)
        gl.glPopMatrix()

        // Отрисовка Меркурия
        gl.glPushMatrix()
        gl.glRotatef(angleMercuryOrbit, 0.0f, 1.0f, 0.0f) // Вращение вокруг Солнца
        gl.glTranslatef(2.0f, 0.0f, 0.0f) // Позиция Меркурия
        mercury.draw(gl)
        if(selectedPlanet == 1) {
            gl.glScalef(0.5f, 0.5f, 0.5f)
            cube.draw(gl, alpha)
            gl.glScalef(1f, 1f, 1f)
        }
        gl.glPopMatrix()

        // Отрисовка Венеры
        gl.glPushMatrix()
        gl.glRotatef(angleVenusOrbit, 0.0f, 1.0f, 0.0f) // Вращение вокруг Солнца
        gl.glTranslatef(4.0f, 0.0f, 0.0f) // Позиция Венеры
        venus.draw(gl)
        if(selectedPlanet == 2) {
            gl.glScalef(0.5f, 0.5f, 0.5f)
            cube.draw(gl, alpha)
            gl.glScalef(1f, 1f, 1f)
        }
        gl.glPopMatrix()

        // Отрисовка Земли
        gl.glPushMatrix()
        gl.glRotatef(angleEarthOrbit, 0.0f, 1.0f, 0.0f) // Вращение вокруг Солнца
        gl.glTranslatef(6.0f, 0.0f, 0.0f) // Позиция Земли
        earth.draw(gl)
        if(selectedPlanet == 3) {
            gl.glScalef(0.5f, 0.5f, 0.5f)
            cube.draw(gl, alpha)
            gl.glScalef(1f, 1f, 1f)
        }

        // Отрисовка Луны
        gl.glPushMatrix()
        gl.glRotatef(angleMoon, 1.0f, 0.0f, 0.0f) // Вращение Луны вокруг своей оси
        gl.glTranslatef(0.0f, 0.5f, 0.0f) // Позиция Луны относительно Земли (уменьшено)
        moon.draw(gl)
        if(selectedPlanet == 4) {
            gl.glScalef(0.4f, 0.4f, 0.4f)
            cube.draw(gl, alpha)
            gl.glScalef(1f, 1f, 1f)
        }
        gl.glPopMatrix()
        gl.glPopMatrix()

        // Отрисовка Марса
        gl.glPushMatrix()
        gl.glRotatef(angleMarsOrbit, 0.0f, 1.0f, 0.0f) // Вращение вокруг Солнца
        gl.glTranslatef(7.0f, 0.0f, 0.0f) // Позиция Марса
        mars.draw(gl)
        if(selectedPlanet == 5) {
            gl.glScalef(0.5f, 0.5f, 0.5f)
            cube.draw(gl, alpha)
            gl.glScalef(1f, 1f, 1f)
        }
        gl.glPopMatrix()

        // Отрисовка Юпитера
        gl.glPushMatrix()
        gl.glRotatef(angleJupiterOrbit, 0.0f, 1.0f, 0.0f) // Вращение вокруг Солнца
        gl.glTranslatef(8.0f, 0.0f, 0.0f) // Позиция Юпитера
        jupiter.draw(gl)
        if(selectedPlanet == 6) {
            gl.glScalef(0.5f, 0.5f, 0.5f)
            cube.draw(gl, alpha)
            gl.glScalef(1f, 1f, 1f)
        }
        gl.glPopMatrix()

        // Отрисовка Сатурна
        gl.glPushMatrix()
        gl.glRotatef(angleSaturnOrbit, 0.0f, 1.0f, 0.0f) // Вращение вокруг Солнца
        gl.glTranslatef(10.0f, 0.0f, 0.0f) // Позиция Сатурна
        saturn.draw(gl)
        if(selectedPlanet == 7) {
            gl.glScalef(0.8f, 0.8f, 0.8f)
            cube.draw(gl, alpha)
            gl.glScalef(1f, 1f, 1f)
        }
        gl.glPopMatrix()

        // Отрисовка Урана
        gl.glPushMatrix()
        gl.glRotatef(angleUranusOrbit, 0.0f, 1.0f, 0.0f) // Вращение вокруг Солнца
        gl.glTranslatef(12.0f, 0.0f, 0.0f) // Позиция Урана
        uranus.draw(gl)
        if(selectedPlanet == 8) {
            gl.glScalef(0.5f, 0.5f, 0.5f)
            cube.draw(gl, alpha)
            gl.glScalef(1f, 1f, 1f)
        }
        gl.glPopMatrix()

        // Отрисовка Нептуна
        gl.glPushMatrix()
        gl.glRotatef(angleNeptuneOrbit, 0.0f, 1.0f, 0.0f) // Вращение вокруг Солнца
        gl.glTranslatef(14.0f, 0.0f, 0.0f) // Позиция Нептуна
        neptune.draw(gl)
        if(selectedPlanet == 9) {
            gl.glScalef(0.5f, 0.5f, 0.5f)
            cube.draw(gl, alpha)
            gl.glScalef(1f, 1f, 1f)
        }
        gl.glPopMatrix()

        // Обновляем позицию черной дыры
        blackHolePosition[0] += blackHoleSpeed // Двигаем вдоль оси X
        blackHolePosition[2] = sin(alphaAngle*5)*3

        // Если черная дыра выходит за пределы системы, сбрасываем позицию
                if (blackHolePosition[0] > 20.0f) {
                    blackHolePosition[0] = -20.0f
                }

        // Рисуем черную дыру
                gl.glPushMatrix()
                gl.glTranslatef(blackHolePosition[0], blackHolePosition[1], blackHolePosition[2])
                blackHole.draw(gl)
                gl.glPopMatrix()

        angleSun += 0.1f
        angleEarth += 2.0f
        angleMoon += 2.0f
        angleEarthOrbit += 0.2f
        angleVenusOrbit += 0.3f
        angleMarsOrbit += 0.2f
        angleJupiterOrbit += 0.1f
        angleSaturnOrbit += 0.05f
        angleMercuryOrbit += 0.4f
        angleUranusOrbit += 0.05f
        angleNeptuneOrbit += 0.03f
        alphaAngle = if (alphaAngle > 6.28f) 0.0f else alphaAngle + 0.01f
        alpha = 0.5f*abs(sin(alphaAngle))
    }
}


