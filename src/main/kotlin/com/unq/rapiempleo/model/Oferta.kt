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
    @Enumerated(EnumType.STRING)
    var estado : EstadoOferta,
    var sueldoMin : Int,
    var sueldoMax : Int,
    var ubicacion : String,
    @ElementCollection
    val cvPostulantes: MutableList<PostulacionCv> = mutableListOf()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id_oferta: Long? = null

    @ManyToOne
    @JoinColumn(name = "id_ofertante")
    var ofertante: Ofertante? = null
}