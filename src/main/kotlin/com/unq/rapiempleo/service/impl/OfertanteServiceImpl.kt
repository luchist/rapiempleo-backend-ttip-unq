package com.unq.rapiempleo.service.impl


import com.unq.rapiempleo.dto.OfertanteDTO
import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import com.unq.rapiempleo.exceptions.OfertanteNotFoundException
import com.unq.rapiempleo.model.Ofertante
import com.unq.rapiempleo.repository.OfertanteRepository
import com.unq.rapiempleo.service.OfertanteService
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class OfertanteServiceImpl (
    private val ofertanteRepository: OfertanteRepository,
    private val passwordEncoder: PasswordEncoder
) : OfertanteService{

    @Transactional
    override fun recuperarOfertante(idOfertante: Long): OfertanteDTO {
        val ofertante = ofertanteRepository.findById(idOfertante).orElseThrow { throw NullPointerException("No existe ese ofertante") }
        return OfertanteDTO.desdeModelo(ofertante)
    }

    override fun registroOfertante(ofertanteRegistro: OfertanteRegistryDTO) {
        val encodedPassword = passwordEncoder.encode(ofertanteRegistro.password)

        val nuevoOfertante = Ofertante(ofertanteRegistro.nombre,
            ofertanteRegistro.empresa,
            ofertanteRegistro.email,
            encodedPassword!!)
        ofertanteRepository.save(nuevoOfertante)
    }


    override fun eliminarNotificacion(idOfertante: Long, idNotificacion: Long) {
        val userToModify = ofertanteRepository.findById(idOfertante).orElseThrow { throw OfertanteNotFoundException() }
        userToModify!!.eliminarNotificacionEn(idNotificacion.toInt())
        if (userToModify.avisosPostulacion.isEmpty()) {
            userToModify.nuevaNotifcacion = false
        }
        ofertanteRepository.save(userToModify)
    }

}