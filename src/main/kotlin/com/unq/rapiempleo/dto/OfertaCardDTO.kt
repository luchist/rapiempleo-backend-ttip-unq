package com.unq.rapiempleo.dto

import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.model.Oferta

class OfertaCardDTO (
    var id : Long,
    var titulo : String,
    var empresa : String,
    var modalidad : Modalidad,
    var sueldoMin : Int,
    var sueldoMax : Int,
    var ubicacion : String,
    var favorito : Boolean
) {
companion object {
    fun desdeModelo (oferta : Oferta) : OfertaCardDTO {
        var ofertaDTOres = OfertaCardDTO(
            id = oferta.id_oferta!!,
            titulo = oferta.titulo,
            empresa = oferta.empresa,
            modalidad = oferta.modalidad,
            sueldoMin = oferta.sueldoMin,
            sueldoMax = oferta.sueldoMax,
            ubicacion = oferta.ubicacion,
            favorito = oferta.favorito
        )
        return ofertaDTOres
    }
}

}