package com.unq.rapiempleo.service.impl

import com.unq.rapiempleo.dto.PostulanteDTO
import com.unq.rapiempleo.dto.LoginResponseDTO
import com.unq.rapiempleo.dto.PostulanteRegistryDTO
import com.unq.rapiempleo.dto.UsuarioLoginDTO
import com.unq.rapiempleo.exceptions.InvalidEmailException
import com.unq.rapiempleo.exceptions.InvalidPasswordException
import com.unq.rapiempleo.exceptions.OfferNotFoundException
import com.unq.rapiempleo.exceptions.UserNotAvailable

import com.unq.rapiempleo.model.Curriculum
import com.unq.rapiempleo.model.Postulante
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.PostulanteRepository
import com.unq.rapiempleo.security.JwtTokenProvider
import com.unq.rapiempleo.service.PostulanteService
import com.unq.rapiempleo.service.auxiliar.PostulationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class PostulanteServiceImpl (
    private val ofertaRepository: OfertaRepository,
    private val postulanteRepository: PostulanteRepository,
    private val publisher : ApplicationEventPublisher,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
) : PostulanteService {

    override fun getPostulante(idPostulante: Long) : PostulanteDTO {
        val postulante = postulanteRepository.findById(idPostulante).orElseThrow { throw NullPointerException() }
        return PostulanteDTO.desdeModelo(postulante)
    }

    override fun postularEnOferta(idOferta: Long, idPostulante: Long) {
        val ofertaOpt = ofertaRepository.findById(idOferta).orElseThrow{ throw OfferNotFoundException() }
        val postulanteop = postulanteRepository.findById(idPostulante).orElseThrow { throw UserNotAvailable() }

        ofertaOpt.postulantes.add(postulanteop)
        postulanteop.postulaciones.add(ofertaOpt)
        ofertaRepository.save(ofertaOpt)
        postulanteRepository.save(postulanteop)

        publisher.publishEvent(
            PostulationEvent(
                ofertaOpt.ofertante!!.id_ofertante!!,
                ofertaOpt.titulo)
        )
    }

    override fun getPreferencias(idPostulante: Long) : String {
        val postulante = postulanteRepository.findById(idPostulante).orElseThrow { throw NullPointerException() }
        return postulante.preferencias
    }

    override fun registrarUserPostulante(postulanteRegistro: PostulanteRegistryDTO) {
        val encodedPassword = passwordEncoder.encode(postulanteRegistro.password)
        val nuevoPostulante = Postulante(
            postulanteRegistro.nombre,
            Curriculum(postulanteRegistro.nombre, "37.465.132"),
            "Estoy en la búsqueda de un trabajo que...",
            postulanteRegistro.email,
            encodedPassword!!
        )
        postulanteRepository.save(nuevoPostulante)
    }

    override fun loginPostulante(usuarioLoginData: UsuarioLoginDTO): LoginResponseDTO {
        val postulante = postulanteRepository.findByEmail(usuarioLoginData.email) ?: throw InvalidEmailException()

        if (!passwordEncoder.matches(usuarioLoginData.password, postulante.password)) {
            throw InvalidPasswordException()
        }
        val token = jwtTokenProvider.generateToken(usuarioLoginData.email)
        return LoginResponseDTO(postulante.id_postulante!!, postulante.nombrPostulante, true, token)
    }
}