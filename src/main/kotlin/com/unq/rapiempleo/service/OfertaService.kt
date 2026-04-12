package com.unq.rapiempleo.service

import com.unq.rapiempleo.dto.OfertaCardDTO
import com.unq.rapiempleo.dto.OfertaDTO
import org.springframework.stereotype.Service

@Service
interface OfertaService {

    fun recuperarOferta (idOferta : Long) : OfertaDTO
    fun recuperarTodasLasOfertas () : List<OfertaCardDTO>
    fun buscarOfertas (nombreOferta : String) : List<OfertaCardDTO>
}