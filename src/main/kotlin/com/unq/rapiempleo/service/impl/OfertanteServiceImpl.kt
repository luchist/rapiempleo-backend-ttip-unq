package com.unq.rapiempleo.service.impl

import com.unq.rapiempleo.dto.OfertanteDTO
import com.unq.rapiempleo.repository.OfertanteRepository
import com.unq.rapiempleo.service.OfertanteService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service


@Service
class OfertanteServiceImpl (
    private val ofertanteRepository: OfertanteRepository,
) : OfertanteService{

    @Transactional
    override fun recuperarOfertante(idOfertante: Long): OfertanteDTO {
        val ofertante = ofertanteRepository.findById(idOfertante).orElseThrow { throw NullPointerException("No existe ese ofertante") }
        return OfertanteDTO.desdeModelo(ofertante)
    }

}