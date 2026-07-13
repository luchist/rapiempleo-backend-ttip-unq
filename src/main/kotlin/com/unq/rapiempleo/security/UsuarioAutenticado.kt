package com.unq.rapiempleo.security

import org.springframework.security.core.AuthenticatedPrincipal

data class UsuarioAutenticado(
    val id: Long,
    val email: String,
    val esPostulante: Boolean,
) : AuthenticatedPrincipal {
    override fun getName(): String = email
}
