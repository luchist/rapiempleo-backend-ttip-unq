package com.unq.rapiempleo.service.impl

import com.unq.rapiempleo.dto.OfertaCardDTO
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.service.SearchService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SearchServiceImpl(
    private val ofertaRepository: OfertaRepository
) : SearchService {

    @Transactional
    override fun searchByTitle(title: String): List<OfertaCardDTO> {
        val ofertasBuscadas = ofertaRepository.findByTituloContainingIgnoreCase(title)
        return ofertasBuscadas.map { oferta -> OfertaCardDTO.desdeModelo(oferta) }
    }
}