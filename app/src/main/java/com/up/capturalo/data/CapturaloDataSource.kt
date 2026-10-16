package com.up.capturalo.data

import com.up.capturalo.data.models.Captura
import com.up.capturalo.data.models.Materia

class CapturaloDataSource {
    val materiasIniciales = mutableListOf(
        Materia(1,"Calculo Multivariable", "Prof. Sirgei", "00FF00")
        ,
        Materia(2,"Calculo Integral", "Prof. Sirgei", "00FF00")
        ,
        Materia(3,"Calculo Vectorial", "Prof. Sirgei", "00FF00")
    )

    val capturasIniciales = mutableListOf(
        Captura(1,1, System.currentTimeMillis())
        ,
        Captura(2,2, System.currentTimeMillis())
        ,
        Captura(3,3, System.currentTimeMillis())
    )
}