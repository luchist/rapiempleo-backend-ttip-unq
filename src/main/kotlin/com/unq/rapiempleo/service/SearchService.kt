package com.unq.rapiempleo.service

import com.unq.rapiempleo.dto.OfertaCardDTO
import org.springframework.stereotype.Service

@Service
interface SearchService {
    fun busquedaInteligente(
        q: String?,
        idPostulante: Long?
    ): List<OfertaCardDTO>
}
