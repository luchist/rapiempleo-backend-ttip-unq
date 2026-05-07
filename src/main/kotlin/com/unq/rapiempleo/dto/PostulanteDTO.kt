package com.unq.rapiempleo.dto

import com.unq.rapiempleo.model.Postulante

class PostulanteDTO (
    var id : Long,
    var nombre : String,
    var preferencia : String
)
{
    companion object{
        fun desdeModelo( postulante : Postulante) : PostulanteDTO {
            val postulanteDTORes = PostulanteDTO (
                id = postulante.id_postulante!!,
                nombre = postulante.cv.nombre,
                preferencia = postulante.preferencias
            )
            return postulanteDTORes
        }
    }
}