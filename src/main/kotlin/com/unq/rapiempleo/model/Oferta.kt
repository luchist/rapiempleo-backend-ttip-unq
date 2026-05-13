package com.unq.rapiempleo.model

import jakarta.persistence.*


@Entity
class Oferta (
    var titulo : String,
    var empresa : String,
    @Column(columnDefinition = "TEXT")
    var descripcion : String,
    @Enumerated(EnumType.STRING)
    var modalidad : Modalidad,
    var estado : String,
    var sueldoMin : Int,
    var sueldoMax : Int,
    var ubicacion : String,
    var favorito : Boolean,
    @ElementCollection
    @OrderBy("uploadedAt ASC")
    val cvPostulantes: MutableList<CvEntry> = mutableListOf()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id_oferta: Long? = null

    @ElementCollection
    var postulatesOferta : MutableList<String> = mutableListOf()

    @ManyToMany
    var postulantes : MutableList<Postulante> = mutableListOf()

    @ManyToOne
    @JoinColumn(name = "id_ofertante")
    var ofertante: Ofertante? = null
}