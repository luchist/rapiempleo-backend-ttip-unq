package com.unq.rapiempleo.dto

import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.model.Oferta

class OfertaDTO (
    var id : Long,
    var titulo : String,
    var empresa : String,
    var descripcion : String,
    var modalidad : Modalidad,
    var estado : String,
    var remuneracion : Double,
    var ubicacion : String
) {
companion object {
    fun desdeModelo (oferta : Oferta) : OfertaDTO {
        var ofertaDTOres = OfertaDTO(
            id = oferta.id_oferta!!,
            titulo = oferta.titulo,
            empresa = oferta.empresa,
            descripcion = oferta.descripcion,
            modalidad = oferta.modalidad,
            estado = oferta.estado,
            remuneracion = oferta.remuneracion,
            ubicacion = oferta.ubicacion
        )
        return ofertaDTOres
    }
}

}