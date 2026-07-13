package com.unq.rapiempleo.dto

import com.unq.rapiempleo.model.EstadoOferta
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
    var estado : EstadoOferta,
    var favorito : Boolean
) {
    companion object {
        // favorito depende del postulante que consulta, no de la oferta, lo recibimos resuelto en vez de
        // leerlo del modelo.
        fun desdeModelo (oferta : Oferta, favorito : Boolean = false) : OfertaCardDTO {
            var ofertaDTOres = OfertaCardDTO(
                id = oferta.id_oferta!!,
                titulo = oferta.titulo,
                empresa = oferta.empresa,
                modalidad = oferta.modalidad,
                sueldoMin = oferta.sueldoMin,
                sueldoMax = oferta.sueldoMax,
                ubicacion = oferta.ubicacion,
                estado = oferta.estado,
                favorito = favorito
            )
            return ofertaDTOres
        }
    }
}
