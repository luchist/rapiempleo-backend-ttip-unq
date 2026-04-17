package com.unq.rapiempleo.dto

import com.unq.rapiempleo.model.Ofertante


class OfertanteDTO (
    var id : Long,
    var nombre : String,
    var empresa: String,
    var nuevaNotifcacion : Boolean,
    var avisoNuevaOferta : String,
    var ofertasCreadas : List<OfertaCreadaDTO>
) {
    companion object {
        fun desdeModelo (ofertante : Ofertante) : OfertanteDTO {
            val ofertaCreadaDTOres = OfertanteDTO (
                id = ofertante.id_ofertante!!,
                nombre = ofertante.nombre,
                empresa = ofertante.empresa,
                nuevaNotifcacion = ofertante.nuevaNotifcacion,
                avisoNuevaOferta = ofertante.avisoNuevaOferta,
                ofertasCreadas = ofertante.ofertasCreadas.map { oferta -> OfertaCreadaDTO.desdeModelo(oferta) }
            )
            return ofertaCreadaDTOres
        }
    }
}
