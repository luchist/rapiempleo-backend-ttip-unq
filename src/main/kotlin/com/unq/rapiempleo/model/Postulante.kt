package com.unq.rapiempleo.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import org.hibernate.internal.util.collections.CollectionHelper.listOf

@Entity
class Postulante (
    var cv : Curriculum,

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id_postulante: Long? = null

    @ManyToMany
    var postulaciones : MutableList<Oferta> = mutableListOf()
}