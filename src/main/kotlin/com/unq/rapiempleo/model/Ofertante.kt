package com.unq.rapiempleo.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Ofertante (
    var nombreOfertante : String,
    var empresa : String,
    var email: String,
    var password : String,
    var nuevaNotifcacion : Boolean = false,
    var avisosNuevaOferta : MutableList<String> = mutableListOf()
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id_ofertante: Long? = null

    //@OneToMany
    //@JoinColumn(name = "id_ofertante")
    //var ofertasCreadas : MutableList<Oferta> = mutableListOf()

    @OneToMany(mappedBy = "ofertante")
    var ofertasCreadas: MutableList<Oferta> = mutableListOf()

    fun addNuevaNotificacion( tituloOferta: String ) {
        avisosNuevaOferta.add(0, tituloOferta)
    }


}