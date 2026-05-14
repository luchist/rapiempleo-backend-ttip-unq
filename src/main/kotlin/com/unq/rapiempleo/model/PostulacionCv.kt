package com.unq.rapiempleo.model

import jakarta.persistence.Embeddable

@Embeddable
class PostulacionCv (
    val id_postulante : Long,
    val cvPathPostulacion : String,
    val id_oferta : Long,
    var cvVisto : Boolean = false,
) {
}