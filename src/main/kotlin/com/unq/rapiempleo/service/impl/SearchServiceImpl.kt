package com.unq.rapiempleo.service.impl

import com.unq.rapiempleo.dto.OfertaCardDTO
import com.unq.rapiempleo.model.EstadoOferta
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

    // Characters that are operators in MySQL boolean full-text mode. Stripped from user input so a stray
    // character can't break the query or change its meaning. The native query is parameterized, so this is
    // about query correctness, not SQL injection.
    private val booleanOperatorChars = Regex("""[+\-><()~*"@]""")

    @Transactional
    override fun busquedaInteligente(q: String?, idPostulante: Long?): List<OfertaCardDTO> {
        val terminoBusqueda = construirTerminoBooleano(q)

        val ofertas = if (terminoBusqueda == null) {
            ofertaRepository.findByEstado(EstadoOferta.Abierto)
        } else {
            ofertaRepository.busquedaInteligente(terminoBusqueda)
        }

        val resultado = ofertas.map { OfertaCardDTO.desdeModelo(it) }
        if (idPostulante != null) {
            val favoritos = postulanteRepository.favoritosDelPostulante(idPostulante)
            resultado.forEach { oferta -> if (favoritos.contains(oferta.id)) oferta.favorito = true }
        }
        return resultado
    }

    // Turns free text into a boolean-mode term string with a prefix wildcard per word, e.g.
    // "desarroll front" -> "desarroll* front*". Returns null when there is nothing to search for, which
    // signals the caller to fall back to listing every open offer.
    private fun construirTerminoBooleano(q: String?): String? {
        val limpio = q?.replace(booleanOperatorChars, " ")?.trim().orEmpty()
        if (limpio.isEmpty()) return null

        val terminos = limpio.split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .map { "$it*" }

        val terminoFinal = terminos.takeIf { it.isNotEmpty() }?.joinToString(" ")

        return terminoFinal
    }
}
