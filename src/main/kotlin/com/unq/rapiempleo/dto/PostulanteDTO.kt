package com.unq.rapiempleo.dto

import com.unq.rapiempleo.model.Postulante

class PostulanteDTO (
    var id : Long,
    var nombre : String,
    var email : String,
    var preferencias : String,
    var ofertasFavoritas : List<OfertaCardDTO>

) {
    companion object {
        fun desdeModelo(postulante: Postulante): PostulanteDTO {
            val postulanteRec = PostulanteDTO(
                id = postulante.id_postulante!!,
                nombre = postulante.nombrPostulante,
                email = postulante.email,
                preferencias = postulante.preferencias,
                ofertasFavoritas = postulante.favoritos.map { oferta -> OfertaCardDTO.desdeModelo(oferta) }
            )
            return postulanteRec
        }
    }
}
