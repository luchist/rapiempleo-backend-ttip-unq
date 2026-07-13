package com.unq.rapiempleo.dto


import com.unq.rapiempleo.model.EstadoOferta
import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.model.Oferta

class OfertaDTO (
    var id : Long,
    var titulo : String,
    var empresa : String,
    var descripcion : String,
    var modalidad : Modalidad,
    var estado : EstadoOferta,
    var sueldoMin : Int,
    var sueldoMax : Int,
    var ubicacion : String,
    var favorito : Boolean,
    var yaPostulado : Boolean
) {
    companion object {
        // favorito y yaPostulado dependen del postulante que consulta, no de la oferta, los recibimos
        // resueltos en vez de leerlos del modelo.
        fun desdeModelo (oferta : Oferta, yaPostulado: Boolean, favorito: Boolean) : OfertaDTO {
            val ofertaDTOres = OfertaDTO(
                id = oferta.id_oferta!!,
                titulo = oferta.titulo,
                descripcion = oferta.descripcion,
                empresa = oferta.empresa,
                modalidad = oferta.modalidad,
                estado = oferta.estado,
                sueldoMin = oferta.sueldoMin,
                sueldoMax = oferta.sueldoMax,
                ubicacion = oferta.ubicacion,
                favorito = favorito,
                yaPostulado = yaPostulado
            )
            return ofertaDTOres
        }
    }

}
