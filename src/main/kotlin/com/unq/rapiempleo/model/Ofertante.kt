package com.unq.rapiempleo.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Ofertante (
    var nuevaNotifcacion : Boolean = false,
    var avisoNuevaOferta : String,
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id_ofertante: Long? = null

    //@ManyToOne
    //var ofertasCreadas : MutableList<Oferta> = mutableListOf()


}