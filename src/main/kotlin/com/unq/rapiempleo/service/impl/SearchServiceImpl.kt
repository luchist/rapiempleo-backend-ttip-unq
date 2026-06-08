package com.unq.rapiempleo.service.impl

import com.unq.rapiempleo.dto.OfertaCardDTO
import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.PostulanteRepository
import com.unq.rapiempleo.service.SearchService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SearchServiceImpl(
    private val ofertaRepository: OfertaRepository,
    private val postulanteRepository: PostulanteRepository
) : SearchService {

    @Transactional
    override fun searchByTitle(title: String): List<OfertaCardDTO> {
        val ofertasBuscadas = ofertaRepository.findByTituloContainingIgnoreCase(title)
        return ofertasBuscadas.map { oferta -> OfertaCardDTO.desdeModelo(oferta) }
    }

    @Transactional
    override fun buscarConFiltros(
        titulo: String?,
        empresa: String?,
        modalidad: String?,
        ubicacion: String?,
        idPostulante: Long?
    ): List<OfertaCardDTO> {
        val modalidadEnum: Modalidad? = modalidad?.let {
            runCatching { Modalidad.valueOf(it.replaceFirstChar(Char::uppercase)) }.getOrNull()
        }
        val resultados = ofertaRepository.buscarConFiltros(
            titulo = titulo?.ifBlank { null },
            empresa = empresa?.ifBlank { null },
            modalidad = modalidadEnum,
            ubicacion = ubicacion?.ifBlank { null }
        )
        println(idPostulante)
        var resultadoSegunUser = resultados.map { OfertaCardDTO.desdeModelo(it) }
        if (idPostulante != null) {
            val favoritos = postulanteRepository.favoritosDelPostulante(idPostulante)
            resultadoSegunUser.forEach { oferta -> if (favoritos.contains(oferta.id))  oferta.favorito = true }
            return resultadoSegunUser
        }
        return resultadoSegunUser
    }
}