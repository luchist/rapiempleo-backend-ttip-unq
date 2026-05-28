package com.unq.rapiempleo.model

import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.OrderBy

@Entity
class Postulante (
    var nombrPostulante : String,
    var preferencias : String,
    var email : String,
    var password : String,
    @ElementCollection
    @OrderBy("uploadedAt ASC")
    val cvEntries: MutableList<CvEntry> = mutableListOf(),
    @ElementCollection
    var notificacionesCv : MutableList<String> = mutableListOf()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id_postulante: Long? = null

    var cvFavorito: String? = null

    var fotoPerfil: String? = null

    @ManyToMany
    var postulaciones : MutableList<Oferta> = mutableListOf()

    @ManyToMany
    var favoritos : MutableList<Oferta> = mutableListOf()

    fun eliminarNotificacionCvVisto (idNotificacion : Int) {
        val listToModify = notificacionesCv

        if (idNotificacion >= listToModify.size || idNotificacion < 0)
            throw IllegalArgumentException("Id de notificación inválido")

        listToModify.removeAt(idNotificacion)
        this.notificacionesCv = listToModify
    }
}