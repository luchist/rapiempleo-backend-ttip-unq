package com.unq.rapiempleo.dto

import com.unq.rapiempleo.model.Ofertante


class OfertanteDTO (
    var id : Long,
    var nombre : String,
    var empresa: String,
    var cantidadNotifacion: Int,
    var nuevaNotifcacion : Boolean,
    var avisoNuevaOferta : List<String>,
    var ofertasCreadas : List<OfertaCreadaDTO>
) {
    companion object {
        fun desdeModelo (ofertante : Ofertante) : OfertanteDTO {
            val ofertaCreadaDTOres = OfertanteDTO (
                id = ofertante.id_ofertante!!,
                nombre = ofertante.nombreOfertante,
                empresa = ofertante.empresa,
                cantidadNotifacion = ofertante.avisosNuevaOferta.size,
                nuevaNotifcacion = ofertante.nuevaNotifcacion,
                avisoNuevaOferta = ofertante.avisosNuevaOferta,
                ofertasCreadas = ofertante.ofertasCreadas.map { oferta -> OfertaCreadaDTO.desdeModelo(oferta) }
            )
            return ofertaCreadaDTOres
        }
    }
}
