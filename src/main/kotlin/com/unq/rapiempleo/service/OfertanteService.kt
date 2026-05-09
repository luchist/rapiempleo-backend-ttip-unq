package com.unq.rapiempleo.service

import com.unq.rapiempleo.dto.LoginResponseDTO
import com.unq.rapiempleo.dto.OfertanteDTO
import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import com.unq.rapiempleo.dto.UsuarioLoginDTO
import org.springframework.stereotype.Service

@Service
interface OfertanteService {
    fun recuperarOfertante (idOfertante : Long) : OfertanteDTO
    fun registroOfertante (ofertanteRegistro : OfertanteRegistryDTO)
    fun loginOfertante (usuarioLoginData: UsuarioLoginDTO) : LoginResponseDTO
    fun eliminarNotificacion (idOfertante : Long, idNotificacion: Long)
}