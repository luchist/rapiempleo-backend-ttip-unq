package com.unq.rapiempleo.service

import com.unq.rapiempleo.dto.OfertaCardDTO
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable

@Service
interface SearchService {
    fun searchByTitle(@PathVariable title : String) : List<OfertaCardDTO>

    fun buscarConFiltros(
        titulo: String?,
        empresa: String?,
        modalidad: String?, //para no acoplar el enum
        ubicacion: String?
    ): List<OfertaCardDTO>
}