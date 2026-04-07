package com.unq.rapiempleo.model

import jakarta.persistence.*


@Entity
class Oferta (
    var nombre : String,
    var descripcion : String,
    var modalidad : Modalidad,
    var estado : String,
    var remuneracion : Double
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id_oferta: Long? = null

    @ManyToMany
    var postulantes : MutableList<Postulante> = mutableListOf()
}