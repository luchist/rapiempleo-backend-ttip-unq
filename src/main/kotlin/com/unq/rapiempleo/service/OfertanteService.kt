package com.unq.rapiempleo.service

import com.unq.rapiempleo.dto.OfertanteDTO
import org.springframework.stereotype.Service

@Service
interface OfertanteService {
    fun recuperarOfertante (idOfertante : Long) : OfertanteDTO
}