package com.unq.rapiempleo.service.impl

import com.unq.rapiempleo.dto.OfertaCardDTO
import com.unq.rapiempleo.dto.OfertaDTO
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.service.OfertaService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service


@Service
class OfertaServiceImpl (
    private val ofertaRepository: OfertaRepository
): OfertaService  {

    @Transactional
    override fun recuperarOferta(idOferta: Long): OfertaDTO {
        val oferta = ofertaRepository.findById(idOferta).orElseThrow { throw NullPointerException("No existe la oferta") }
        return OfertaDTO.desdeModelo(oferta)
    }
    @Transactional
    override fun recuperarTodasLasOfertas(): List<OfertaCardDTO> {
        val ofertas = ofertaRepository.findAll()
        return ofertas.map { oferta -> OfertaCardDTO.desdeModelo(oferta) }
    }

    override fun buscarOfertas(nombreOferta: String): List<OfertaCardDTO> {
        val ofertasBuscadas = ofertaRepository.findByTituloContainingIgnoreCase(nombreOferta)
        return ofertasBuscadas.map { oferta -> OfertaCardDTO.desdeModelo(oferta) }
    }
}