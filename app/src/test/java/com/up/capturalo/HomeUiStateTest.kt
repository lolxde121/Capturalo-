package com.up.capturalo

import com.up.capturalo.data.models.Captura
import com.up.capturalo.data.models.Materia
import com.up.capturalo.presentation.viewmodel.HomeUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Pruebas unitarias locales para las transformaciones en memoria de [HomeUiState].
 *
 * Demuestra la verificabilidad del estado de la UI y la separación de responsabilidades (Clean Architecture).
 */
class HomeUiStateTest {

    private val materia1 = Materia(id = 1, nombre = "Cálculo Multivariable", profesor = "Prof. Sirgei", color = "4CAF50")
    private val materia2 = Materia(id = 2, nombre = "Física Clásica", profesor = "Prof. Newton", color = "2196F3")

    private val capturasMock = listOf(
        Captura(id = 1, idMateria = 1, rutaImagen = "/path/1.jpg"),
        Captura(id = 2, idMateria = 1, rutaImagen = "/path/2.jpg"),
        Captura(id = 3, idMateria = 2, rutaImagen = "/path/3.jpg"),
    )

    @Test
    fun totalCapturasPorMateria_calculaConteoCorrectamente() {
        val state = HomeUiState(
            materias = listOf(materia1, materia2),
            todasLasCapturas = capturasMock
        )

        assertEquals(2, state.totalCapturasPorMateria(1))
        assertEquals(1, state.totalCapturasPorMateria(2))
        assertEquals(0, state.totalCapturasPorMateria(99))
    }

    @Test
    fun materiaConMasCapturas_retornaMateriaCorrecta() {
        val state = HomeUiState(
            materias = listOf(materia1, materia2),
            todasLasCapturas = capturasMock
        )

        val lider = state.materiaConMasCapturas
        assertNotNull(lider)
        assertEquals(1, lider?.id)
        assertEquals("Cálculo Multivariable", lider?.nombre)
    }

    @Test
    fun materiasFiltradas_filtraPorNombreOProfesor() {
        val state = HomeUiState(
            materias = listOf(materia1, materia2),
            filtroQuery = "newton"
        )

        assertEquals(1, state.materiasFiltradas.size)
        assertEquals("Física Clásica", state.materiasFiltradas.first().nombre)
    }

    @Test
    fun porcentajeDeCapturas_calculaRelacionCorrecta() {
        val state = HomeUiState(
            materias = listOf(materia1, materia2),
            todasLasCapturas = capturasMock
        )

        // 2 de 3 capturas = 66.66%
        val porcentajeMateria1 = state.porcentajeDeCapturas(1)
        assertEquals(66.66f, porcentajeMateria1, 0.5f)
    }
}
