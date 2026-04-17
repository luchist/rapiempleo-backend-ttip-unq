package com.unq.rapiempleo.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany

@Entity
class Ofertante (
    var nombre : String,
    var empresa : String,
    var nuevaNotifcacion : Boolean = false,
    var avisoNuevaOferta : String,
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id_ofertante: Long? = null

    @OneToMany
    @JoinColumn(name = "id_ofertante")
    var ofertasCreadas : MutableList<Oferta> = mutableListOf()


}