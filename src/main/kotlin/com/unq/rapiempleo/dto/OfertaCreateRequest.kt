package com.unq.rapiempleo.dto

import com.unq.rapiempleo.model.Modalidad

class OfertaCreateRequest(
    var titulo: String,
    var descripcion: String,
    var modalidad: Modalidad,
    var sueldoMin: Int,
    var sueldoMax: Int,
    var ubicacion: String,
)
