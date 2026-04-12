package com.unq.rapiempleo.dto


import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.model.Oferta

class OfertaDTO (
    var id : Long,
    var titulo : String,
    var empresa : String,
    var descripcion : String,
    var modalidad : Modalidad,
    var sueldoMin : Int,
    var sueldoMax : Int,
    var ubicacion : String
) {
    companion object {
        fun desdeModelo (oferta : Oferta) : OfertaDTO {
            val ofertaDTOres = OfertaDTO(
                id = oferta.id_oferta!!,
                titulo = oferta.titulo,
                descripcion = oferta.descripcion,
                empresa = oferta.empresa,
                modalidad = oferta.modalidad,
                sueldoMin = oferta.sueldoMin,
                sueldoMax = oferta.sueldoMax,
                ubicacion = oferta.ubicacion
            )
            return ofertaDTOres
        }
    }

}