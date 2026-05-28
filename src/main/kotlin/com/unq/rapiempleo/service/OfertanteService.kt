package com.unq.rapiempleo.service

import com.unq.rapiempleo.dto.OfertanteDTO
import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import org.springframework.stereotype.Service

@Service
interface OfertanteService {
    fun recuperarOfertante (idOfertante : Long) : OfertanteDTO
    fun registroOfertante (ofertanteRegistro : OfertanteRegistryDTO)
    fun eliminarNotificacion (idOfertante : Long, idNotificacion: Long)
    fun actualizarImagenPerfil(idOfertante: Long, fotoPath: String)
}