package com.unq.rapiempleo.dto

import com.unq.rapiempleo.model.EstadoPostulacion
import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.model.PostulacionEstado

class PostulacionBoardItemDTO (
    var id_oferta: Long,
    var titulo : String,
    var empresa : String,
    var modalidad : Modalidad,
    var estado : EstadoPostulacion
){
    companion object{
        fun desdeModelo(postulacionEstado: PostulacionEstado): PostulacionBoardItemDTO {
            return PostulacionBoardItemDTO(
                id_oferta  = postulacionEstado.oferta.id_oferta!!,
                titulo     = postulacionEstado.oferta.titulo,
                empresa    = postulacionEstado.oferta.empresa,
                modalidad  = postulacionEstado.oferta.modalidad,
                estado     = postulacionEstado.estado
            )
        }
    }
}