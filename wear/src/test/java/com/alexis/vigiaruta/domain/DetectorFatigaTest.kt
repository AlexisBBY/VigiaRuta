package com.alexis.vigiaruta.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DetectorFatigaTest {

    private fun detector() = DetectorFatiga(
        duracionCalibracionMs = 5_000,
        ventanaFatigaMs = 3_000
    )

    private fun DetectorFatiga.alimentar(
        desdeSeg: Int,
        hastaSeg: Int,
        pulso: Int,
        movimiento: Float
    ): ResultadoDeteccion {
        var resultado: ResultadoDeteccion? = null
        for (s in desdeSeg..hastaSeg) {
            resultado = procesar(LecturaSensor(pulso, movimiento, s * 1000L))
        }
        return resultado!!
    }

    @Test
    fun `calcula la linea base al terminar la calibracion`() {
        val d = detector()
        d.alimentar(0, 5, pulso = 80, movimiento = 1f)
        assertEquals(80, d.pulsoBase)
    }

    @Test
    fun `detecta fatiga con pulso bajo y muneca quieta`() {
        val d = detector()
        d.alimentar(0, 5, pulso = 80, movimiento = 1f)
        val r = d.alimentar(6, 9, pulso = 66, movimiento = 0.1f)
        assertTrue(r is ResultadoDeteccion.Fatiga)
    }

    @Test
    fun `no detecta fatiga si la muneca se mueve`() {
        val d = detector()
        d.alimentar(0, 5, pulso = 80, movimiento = 1f)
        val r = d.alimentar(6, 15, pulso = 66, movimiento = 2f)
        assertTrue(r is ResultadoDeteccion.Normal)
    }

    @Test
    fun `ignora lecturas en cero durante la calibracion`() {
        val d = detector()
        d.procesar(LecturaSensor(0, 1f, 0))
        d.alimentar(1, 5, pulso = 70, movimiento = 1f)
        assertEquals(70, d.pulsoBase)
    }
}
