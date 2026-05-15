package com.unq.rapiempleo.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class PostulacionEstado (
    @ManyToOne
    @JoinColumn(name = "oferta_id")
    var oferta: Oferta,

    @ManyToOne
    @JoinColumn(name = "postulante_id")
    var postulante: Postulante,

    var estado: EstadoPostulacion
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id_postulacion_estado: Long? = null
}