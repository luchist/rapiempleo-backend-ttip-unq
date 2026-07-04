package com.unq.rapiempleo.service.impl

import com.unq.rapiempleo.dto.DeleteCVRequestDTO
import com.unq.rapiempleo.dto.OfertaCardDTO
import com.unq.rapiempleo.dto.OfertaDTO
import com.unq.rapiempleo.exceptions.OfferNotFoundException
import com.unq.rapiempleo.exceptions.SavedCVNotFoundException
import com.unq.rapiempleo.model.EstadoOferta
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.PostulacionEstadoRepository
import com.unq.rapiempleo.repository.PostulanteRepository
import com.unq.rapiempleo.service.OfertaService
import jakarta.transaction.Transactional
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service


@Service
class OfertaServiceImpl (
    private val ofertaRepository: OfertaRepository,
    private val postulanteRepository: PostulanteRepository,
    private val postulacionEstadoRepository: PostulacionEstadoRepository
): OfertaService {

    @Transactional
    override fun recuperarOferta(idOferta: Long): OfertaDTO {
        val oferta =
            ofertaRepository.findById(idOferta).orElseThrow { throw OfferNotFoundException() }
        return OfertaDTO.desdeModelo(oferta, ofertaYaPostulada(oferta.id_oferta))
    }

    private fun ofertaYaPostulada(ofertaId: Long?): Boolean {
        val email = SecurityContextHolder.getContext().authentication?.name ?: return false
        val postulante = postulanteRepository.findByEmail(email) ?: return false
        return postulacionEstadoRepository.findByPostulante(postulante)
            .any { postulacion -> postulacion.oferta.id_oferta == ofertaId }
    }

    @Transactional
    override fun recuperarTodasLasOfertas(): List<OfertaCardDTO> {
        return ofertaRepository.findByEstado(EstadoOferta.Abierto)
            .map { oferta -> OfertaCardDTO.desdeModelo(oferta) }
    }

    override fun buscarOfertas(nombreOferta: String): List<OfertaCardDTO> {
        return ofertaRepository.findByTituloContainingIgnoreCaseAndEstado(nombreOferta, EstadoOferta.Abierto)
            .map { oferta -> OfertaCardDTO.desdeModelo(oferta) }
    }

    override fun recuperarTodasLasOfertasYFavoritos(idPostulante: Long): List<OfertaCardDTO> {
        val favoritosPostulante = postulanteRepository.favoritosDelPostulante(idPostulante)
        val ofertasCard = ofertaRepository.findByEstado(EstadoOferta.Abierto)
            .map { oferta -> OfertaCardDTO.desdeModelo(oferta) }
        ofertasCard.forEach { oferta -> if (favoritosPostulante.contains(oferta.id)) oferta.favorito = true }
        return ofertasCard
    }

    override fun eliminarCVPostulacion(cvAEliminar: DeleteCVRequestDTO) {
        val ofertaAModificar = ofertaRepository.findById(cvAEliminar.idOferta)
            .orElseThrow { throw OfferNotFoundException() }

        if (ofertaAModificar.cvPostulantes.none { cv -> cv.id_postulante ==  cvAEliminar.idPostulante }) {
            throw SavedCVNotFoundException()
        }
        ofertaAModificar.cvPostulantes.removeIf { cv -> cv.id_postulante == cvAEliminar.idPostulante }
        ofertaRepository.save(ofertaAModificar)
    }
}
