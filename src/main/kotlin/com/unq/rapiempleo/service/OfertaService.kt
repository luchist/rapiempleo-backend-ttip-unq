package com.unq.rapiempleo.service

import com.unq.rapiempleo.dto.OfertaDTO
import com.unq.rapiempleo.model.Oferta
import org.springframework.stereotype.Service

@Service
interface OfertaService {

    fun recuperarOferta (idOferta : Long) : Oferta
    fun recuperarTodasLasOfertas () : List<OfertaDTO>
    fun buscarOfertas (nombreOferta : String) : List<OfertaDTO>
}