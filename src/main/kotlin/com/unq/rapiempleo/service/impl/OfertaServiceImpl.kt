package com.unq.rapiempleo.service.impl

import com.unq.rapiempleo.dto.OfertaDTO
import com.unq.rapiempleo.model.Oferta
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.service.OfertaService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service


@Service
class OfertaServiceImpl (
    private val ofertaRepository: OfertaRepository
): OfertaService  {

    @Transactional
    override fun recuperarOferta(idOferta: Long): Oferta {
        return ofertaRepository.findById(idOferta).orElseThrow { throw NullPointerException("No existe la oferta") };
    }
    @Transactional
    override fun recuperarTodasLasOfertas(): List<OfertaDTO> {
        val ofertas = ofertaRepository.findAll()
        return ofertas.map { oferta -> OfertaDTO.desdeModelo(oferta) }
    }

    override fun buscarOfertas(nombreOferta: String): List<OfertaDTO> {
        val ofertasBuscadas = ofertaRepository.findByTituloContainingIgnoreCase(nombreOferta)
        return ofertasBuscadas.map { oferta -> OfertaDTO.desdeModelo(oferta) }
    }
}