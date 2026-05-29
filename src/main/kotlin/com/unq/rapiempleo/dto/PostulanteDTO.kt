package com.unq.rapiempleo.dto

import com.unq.rapiempleo.model.NotificationEntry
import com.unq.rapiempleo.model.Postulante

class PostulanteDTO (
    var id : Long,
    var nombre : String,
    var preferencia : String,
    var ofertasFavoritas : List<OfertaCardDTO>,
    var notificacionesCv : List<NotificationEntry>,
    var cvPaths : List<String>,
    var cvFavorito : String?,
    var fotoPerfil : String?
)
{
    companion object{
        fun desdeModelo(postulante : Postulante) : PostulanteDTO {
            val postulanteDTORes = PostulanteDTO (
                id = postulante.id_postulante!!,
                nombre = postulante.nombrPostulante,
                preferencia = postulante.preferencias,
                ofertasFavoritas = postulante.favoritos.map { oferta -> OfertaCardDTO.desdeModelo(oferta) },
                notificacionesCv = postulante.notificacionesCv,
                cvPaths = postulante.cvEntries.map { it.cvPath },
                cvFavorito = postulante.cvFavorito,
                fotoPerfil = postulante.fotoPerfil
            )
            return postulanteDTORes
        }
    }
}
