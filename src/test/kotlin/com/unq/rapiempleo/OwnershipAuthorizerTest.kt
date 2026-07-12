package com.unq.rapiempleo

import com.unq.rapiempleo.model.EstadoOferta
import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.model.Oferta
import com.unq.rapiempleo.model.Ofertante
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.security.OwnershipAuthorizer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.core.Authentication
import java.util.Optional

class OwnershipAuthorizerTest {

    private val ofertaRepository = mock<OfertaRepository>()
    private val autorizacion = OwnershipAuthorizer(ofertaRepository)

    private fun authConDetails(details: Any?): Authentication {
        val auth = mock<Authentication>()
        whenever(auth.details).thenReturn(details)
        return auth
    }

    private fun ofertaDeOfertante(ofertanteId: Long): Oferta {
        val ofertante = Ofertante("Emp", "Empresa", "e@e.com", "pass")
        ofertante.id_ofertante = ofertanteId
        val oferta = Oferta(
            "titulo", "Empresa", "desc", Modalidad.Remoto, EstadoOferta.Abierto, 1, 2, "CABA", favorito = false
        )
        oferta.ofertante = ofertante
        return oferta
    }

    @Test
    fun esUsuarioActualEsTrueCuandoElIdCoincide() {
        Assertions.assertTrue(autorizacion.esUsuarioActual(1L, authConDetails(1L)))
    }

    @Test
    fun esUsuarioActualEsFalseCuandoElIdNoCoincide() {
        Assertions.assertFalse(autorizacion.esUsuarioActual(1L, authConDetails(2L)))
    }

    @Test
    fun esUsuarioActualEsFalseSinAutenticacion() {
        Assertions.assertFalse(autorizacion.esUsuarioActual(1L, null))
    }

    @Test
    fun esUsuarioActualEsFalseSiDetailsNoEsLong() {
        Assertions.assertFalse(autorizacion.esUsuarioActual(1L, authConDetails("no-soy-un-long")))
    }

    @Test
    fun gestionaOfertaEsTrueCuandoElOfertanteEsDuenio() {
        whenever(ofertaRepository.findById(10L)).thenReturn(Optional.of(ofertaDeOfertante(5L)))
        Assertions.assertTrue(autorizacion.gestionaOferta(10L, authConDetails(5L)))
    }

    @Test
    fun gestionaOfertaEsFalseCuandoElOfertanteNoEsDuenio() {
        whenever(ofertaRepository.findById(10L)).thenReturn(Optional.of(ofertaDeOfertante(5L)))
        Assertions.assertFalse(autorizacion.gestionaOferta(10L, authConDetails(99L)))
    }

    @Test
    fun gestionaOfertaEsFalseCuandoLaOfertaNoExiste() {
        whenever(ofertaRepository.findById(10L)).thenReturn(Optional.empty())
        Assertions.assertFalse(autorizacion.gestionaOferta(10L, authConDetails(5L)))
    }

    @Test
    fun gestionaOfertaEsFalseSinAutenticacion() {
        Assertions.assertFalse(autorizacion.gestionaOferta(10L, null))
    }
}
