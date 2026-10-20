package com.up.capturalo.data.dataSource

import com.up.capturalo.data.models.Captura
import com.up.capturalo.data.models.Materia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Fuente de datos local en memoria (RAM).
 * Preparada para migrarse a DataStore/SharedPreferences cambiando únicamente
 * la implementación interna, sin tocar el contrato público.
 */
class CapturaloDataSource {

    // ---------- Seed inicial (dataSeed) ----------

    private val _materias = MutableStateFlow(
        mutableListOf(
            Materia(1, "Cálculo Multivariable", "Prof. Sirgei", "4CAF50"),
            Materia(2, "Cálculo Integral",      "Prof. Sirgei", "2196F3"),
            Materia(3, "Cálculo Vectorial",     "Prof. Sirgei", "9C27B0"),
        )
    )

    private val _capturas = MutableStateFlow(
        mutableListOf(
            Captura(1, 1, "", System.currentTimeMillis()),
            Captura(2, 2, "", System.currentTimeMillis()),
            Captura(3, 3, "", System.currentTimeMillis()),
        )
    )

    // ---------- Contadores de ID auto-incremental ----------

    private var siguienteIdMateria = 4
    private var siguienteIdCaptura = 4

    // ---------- API de Materias ----------

    /** Expone la lista reactiva de materias como flujo de sólo-lectura. */
    fun obtenerMaterias(): StateFlow<List<Materia>> =
        _materias.asStateFlow()

    /** Agrega una nueva materia con ID auto-generado. */
    fun agregarMateria(nombre: String, profesor: String, colorHex: String): Materia {
        val nueva = Materia(
            id       = siguienteIdMateria++,
            nombre   = nombre,
            profesor = profesor,
            color    = colorHex,
        )
        _materias.update { lista ->
            (lista + nueva).toMutableList()
        }
        return nueva
    }

    /** Actualiza una materia existente; no hace nada si el ID no existe. */
    fun actualizarMateria(materia: Materia) {
        _materias.update { lista ->
            lista.map { if (it.id == materia.id) materia else it }.toMutableList()
        }
    }

    /** Elimina la materia con el ID dado; no hace nada si no existe. */
    fun eliminarMateria(id: Int) {
        _materias.update { lista ->
            lista.filter { it.id != id }.toMutableList()
        }
    }

    // ---------- API de Capturas (sin cambios) ----------

    fun obtenerCapturas(): StateFlow<List<Captura>> =
        _capturas.asStateFlow()

    fun agregarCaptura(idMateria: Int, rutaImagen: String): Captura {
        val nueva = Captura(
            id          = siguienteIdCaptura++,
            idMateria   = idMateria,
            rutaImagen  = rutaImagen,
            fecha       = System.currentTimeMillis(),
        )
        _capturas.update { lista ->
            (lista + nueva).toMutableList()
        }
        return nueva
    }

    fun eliminarCaptura(id: Int) {
        _capturas.update { lista ->
            lista.filter { it.id != id }.toMutableList()
        }
    }
}