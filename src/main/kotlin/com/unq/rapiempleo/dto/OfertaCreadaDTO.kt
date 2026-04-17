package com.unq.rapiempleo.dto

import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.model.Oferta

class OfertaCreadaDTO(var id : Long,
                      var titulo : String,
                      var empresa : String,
                      var modalidad : Modalidad,
                      var sueldoMin : Int,
                      var sueldoMax : Int,
                      var ubicacion : String,
) {
    companion object {
        fun desdeModelo (oferta : Oferta) : OfertaCreadaDTO {
            val ofertaCreadaDTOres = OfertaCreadaDTO(
                id = oferta.id_oferta!!,
                titulo = oferta.titulo,
                empresa = oferta.empresa,
                modalidad = oferta.modalidad,
                sueldoMin = oferta.sueldoMin,
                sueldoMax = oferta.sueldoMax,
                ubicacion = oferta.ubicacion,
            )
            return ofertaCreadaDTOres
        }
    }

}