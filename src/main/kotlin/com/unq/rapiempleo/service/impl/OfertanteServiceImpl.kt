package com.unq.rapiempleo.service.impl


import com.unq.rapiempleo.dto.OfertaCreadaDTO
import com.unq.rapiempleo.dto.OfertaCreateRequest
import com.unq.rapiempleo.dto.OfertanteDTO
import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import com.unq.rapiempleo.exceptions.AccessDeniedToFileException
import com.unq.rapiempleo.exceptions.DuplicatedEmailException
import com.unq.rapiempleo.exceptions.OfertanteNotFoundException
import com.unq.rapiempleo.exceptions.UnauthenticatedException
import com.unq.rapiempleo.model.Oferta
import com.unq.rapiempleo.model.Ofertante
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.OfertanteRepository
import com.unq.rapiempleo.repository.PostulanteRepository
import com.unq.rapiempleo.service.OfertanteService
import jakarta.transaction.Transactional
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class OfertanteServiceImpl (
    private val ofertanteRepository: OfertanteRepository,
    private val ofertaRepository: OfertaRepository,
    private val passwordEncoder: PasswordEncoder,
    private val postulanteRepository: PostulanteRepository,
) : OfertanteService{

    @Transactional
    override fun recuperarOfertante(idOfertante: Long): OfertanteDTO {
        val ofertante = ofertanteRepository.findById(idOfertante).orElseThrow { throw OfertanteNotFoundException() }
        return OfertanteDTO.desdeModelo(ofertante)
    }

    override fun registroOfertante(ofertanteRegistro: OfertanteRegistryDTO) {
        val ofertantePorEmail = ofertanteRepository.findByEmail(ofertanteRegistro.email)
        val postulantePorEmail = postulanteRepository.findByEmail(ofertanteRegistro.email)
        if (postulantePorEmail != null || ofertantePorEmail != null) {
            throw DuplicatedEmailException()
        }

        val encodedPassword = passwordEncoder.encode(ofertanteRegistro.password)
        val nuevoOfertante = Ofertante(
            ofertanteRegistro.name,
            ofertanteRegistro.company,
            ofertanteRegistro.email,
            encodedPassword!!)
        ofertanteRepository.save(nuevoOfertante)
    }


    override fun actualizarImagenPerfil(idOfertante: Long, fotoPath: String) {
        val email = SecurityContextHolder.getContext().authentication?.name
            ?: throw UnauthenticatedException()

        val ofertante = ofertanteRepository.findByEmail(email)
            ?: throw OfertanteNotFoundException()

        if (ofertante.id_ofertante != idOfertante) {
            throw AccessDeniedToFileException()
        }

        ofertante.fotoPerfil = fotoPath
        ofertanteRepository.save(ofertante)
    }

    override fun getIdPorEmail(email: String): Long {
        val ofertante = ofertanteRepository.findByEmail(email)
            ?: throw OfertanteNotFoundException()
        return ofertante.id_ofertante!!
    }

    override fun crearOferta(idOfertante: Long, request: OfertaCreateRequest): OfertaCreadaDTO {
        val ofertante = ofertanteRepository.findById(idOfertante)
            .orElseThrow { OfertanteNotFoundException() }

        val oferta = Oferta(
            titulo = request.titulo,
            empresa = ofertante.empresa,
            descripcion = request.descripcion,
            modalidad = request.modalidad,
            estado = "Abierto",
            sueldoMin = request.sueldoMin,
            sueldoMax = request.sueldoMax,
            ubicacion = request.ubicacion,
            favorito = false,
        )

        oferta.ofertante = ofertante
        ofertante.ofertasCreadas.add(oferta)
        ofertaRepository.save(oferta)
        return OfertaCreadaDTO.desdeModelo(oferta)
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