package com.unq.rapiempleo.model

import jakarta.persistence.*


@Entity
class Oferta (
    var titulo : String,
    var empresa : String,
    @Column(columnDefinition = "TEXT")
    var descripcion : String,
    var modalidad : Modalidad,
    var estado : String,
    var sueldoMin : Int,
    var sueldoMax : Int,
    var ubicacion : String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id_oferta: Long? = null

    @ManyToMany
    var postulantes : MutableList<Postulante> = mutableListOf()
}