package com.unq.rapiempleo.security

import com.unq.rapiempleo.repository.OfertaRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component("autorizacion")
class OwnershipAuthorizer(
    private val ofertaRepository: OfertaRepository
) {

    /** Si el id matchea con el usuario autenticado. */
    fun esUsuarioActual(id: Long, authentication: Authentication?): Boolean {
        val callerId = (authentication?.principal as? UsuarioAutenticado)?.id ?: return false
        return callerId == id
    }

    /** Si el idOferta pertenece al ofertante autenticado */
    fun gestionaOferta(idOferta: Long, authentication: Authentication?): Boolean {
        val callerId = (authentication?.principal as? UsuarioAutenticado)?.id ?: return false
        val oferta = ofertaRepository.findById(idOferta).orElse(null)
        return oferta?.ofertante?.id_ofertante == callerId
    }
}
