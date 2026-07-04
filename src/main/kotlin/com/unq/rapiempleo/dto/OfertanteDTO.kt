package com.unq.rapiempleo.dto

import com.unq.rapiempleo.model.CvSummary
import com.unq.rapiempleo.model.Ofertante


class OfertanteDTO (
    var id : Long,
    var nombre : String,
    var empresa: String,
    var cantidadNotifacion: Int,
    var avisosPostulacion : List<String>,
    var ofertasCreadas : List<OfertaCreadaDTO>,
    var cvsGuardados : List<CvSummary>,
    var fotoPerfil : String?
) {
    companion object {
        fun desdeModelo (ofertante : Ofertante) : OfertanteDTO {
            val ofertaCreadaDTOres = OfertanteDTO (
                id = ofertante.id_ofertante!!,
                nombre = ofertante.nombreOfertante,
                empresa = ofertante.empresa,
                cantidadNotifacion = ofertante.avisosPostulacion.size,
                avisosPostulacion = ofertante.avisosPostulacion,
                ofertasCreadas = ofertante.ofertasCreadas.map { oferta -> OfertaCreadaDTO.desdeModelo(oferta) },
                cvsGuardados = ofertante.cvsGuardados,
                fotoPerfil = ofertante.fotoPerfil
            )
            return ofertaCreadaDTOres
        }
    }
}
