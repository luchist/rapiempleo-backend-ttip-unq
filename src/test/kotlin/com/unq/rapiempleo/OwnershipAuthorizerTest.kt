package com.unq.rapiempleo

import com.unq.rapiempleo.model.EstadoOferta
import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.model.Oferta
import com.unq.rapiempleo.model.Ofertante
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.security.OwnershipAuthorizer
import com.unq.rapiempleo.security.UsuarioAutenticado
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.core.Authentication
import java.util.Optional

class OwnershipAuthorizerTest {

    private val ofertaRepository = mock<OfertaRepository>()
    private val autorizacion = OwnershipAuthorizer(ofertaRepository)

    private fun authConPrincipal(principal: Any?): Authentication {
        val auth = mock<Authentication>()
        whenever(auth.principal).thenReturn(principal)
        return auth
    }

    private fun usuario(id: Long, esPostulante: Boolean = true): UsuarioAutenticado =
        UsuarioAutenticado(id, "user$id@test.com", esPostulante)

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
        Assertions.assertTrue(autorizacion.esUsuarioActual(1L, authConPrincipal(usuario(1L))))
    }

    @Test
    fun esUsuarioActualEsFalseCuandoElIdNoCoincide() {
        Assertions.assertFalse(autorizacion.esUsuarioActual(1L, authConPrincipal(usuario(2L))))
    }

    @Test
    fun esUsuarioActualEsFalseSinAutenticacion() {
        Assertions.assertFalse(autorizacion.esUsuarioActual(1L, null))
    }

    @Test
    fun esUsuarioActualEsFalseSiPrincipalNoEsUsuarioAutenticado() {
        Assertions.assertFalse(autorizacion.esUsuarioActual(1L, authConPrincipal("no-soy-un-principal")))
    }

    @Test
    fun gestionaOfertaEsTrueCuandoElOfertanteEsDuenio() {
        whenever(ofertaRepository.findById(10L)).thenReturn(Optional.of(ofertaDeOfertante(5L)))
        Assertions.assertTrue(autorizacion.gestionaOferta(10L, authConPrincipal(usuario(5L, esPostulante = false))))
    }

    @Test
    fun gestionaOfertaEsFalseCuandoElOfertanteNoEsDuenio() {
        whenever(ofertaRepository.findById(10L)).thenReturn(Optional.of(ofertaDeOfertante(5L)))
        Assertions.assertFalse(autorizacion.gestionaOferta(10L, authConPrincipal(usuario(99L, esPostulante = false))))
    }

    @Test
    fun gestionaOfertaEsFalseCuandoLaOfertaNoExiste() {
        whenever(ofertaRepository.findById(10L)).thenReturn(Optional.empty())
        Assertions.assertFalse(autorizacion.gestionaOferta(10L, authConPrincipal(usuario(5L, esPostulante = false))))
    }

    @Test
    fun gestionaOfertaEsFalseSinAutenticacion() {
        Assertions.assertFalse(autorizacion.gestionaOferta(10L, null))
    }

    @Test
    fun gestionaOfertaEsFalseSiPrincipalNoEsUsuarioAutenticado() {
        Assertions.assertFalse(autorizacion.gestionaOferta(10L, authConPrincipal("no-soy-un-principal")))
    }
}
