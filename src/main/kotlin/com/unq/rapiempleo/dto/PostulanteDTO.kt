package com.unq.rapiempleo.dto

import com.unq.rapiempleo.model.Postulante

class PostulanteDTO (
    var id : Long,
    var nombre : String,
    var preferencia : String,
    var ofertasFavoritas : List<OfertaCardDTO>,
    var cvPaths : List<String>
)
{
    companion object{
        fun desdeModelo( postulante : Postulante) : PostulanteDTO {
            val postulanteDTORes = PostulanteDTO (
                id = postulante.id_postulante!!,
                nombre = postulante.cv.nombre,
                preferencia = postulante.preferencias,
                ofertasFavoritas = postulante.favoritos.map { oferta -> OfertaCardDTO.desdeModelo(oferta) },
                cvPaths = postulante.cvEntries.map { it.cvPath }
            )
            return postulanteDTORes
        }
    }
}
