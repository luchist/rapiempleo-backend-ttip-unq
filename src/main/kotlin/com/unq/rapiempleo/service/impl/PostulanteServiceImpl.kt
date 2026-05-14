package com.unq.rapiempleo.service.impl

import com.unq.rapiempleo.dto.PostulanteDTO
import com.unq.rapiempleo.dto.LoginResponseDTO
import com.unq.rapiempleo.dto.PostulanteRegistryDTO
import com.unq.rapiempleo.dto.UsuarioLoginDTO
import com.unq.rapiempleo.exceptions.InvalidEmailException
import com.unq.rapiempleo.exceptions.InvalidPasswordException
import com.unq.rapiempleo.exceptions.OfferNotFoundException
import com.unq.rapiempleo.exceptions.CvLimitExceededException
import com.unq.rapiempleo.exceptions.CvNotFoundException
import com.unq.rapiempleo.exceptions.NoCvAvailableException
import com.unq.rapiempleo.exceptions.PostulanteNotFoundException
import com.unq.rapiempleo.model.CvEntry
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
        val postulante = postulanteRepository.findById(idPostulante)
            .orElseThrow { PostulanteNotFoundException() }
        return PostulanteDTO.desdeModelo(postulante)
    }

    override fun postularEnOferta(idOferta: Long, idPostulante: Long) {
        val ofertaOpt = ofertaRepository.findById(idOferta)
            .orElseThrow{ throw OfferNotFoundException() }
        val postulanteop = postulanteRepository.findById(idPostulante)
            .orElseThrow { throw PostulanteNotFoundException() }

        if (postulanteop.cvEntries.isEmpty()) throw NoCvAvailableException()

        val cvAEnviar = postulanteop.cvEntries
            .find { it.cvPath == postulanteop.cvFavorito }
            ?: postulanteop.cvEntries[0]

        ofertaOpt.postulantes.add(postulanteop)
        ofertaOpt.cvPostulantes.add(cvAEnviar)
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
        val postulante = postulanteRepository.findById(idPostulante)
            .orElseThrow { PostulanteNotFoundException() }
        return postulante.preferencias
    }

    override fun registrarUserPostulante(postulanteRegistro: PostulanteRegistryDTO) {
        val encodedPassword = passwordEncoder.encode(postulanteRegistro.password)
        val nuevoPostulante = Postulante(
            postulanteRegistro.nombre,
            "Estoy buscando trabajo como desarrollador, en la ciudad de Buenos Aires. Prefiero los trabajos con modalidad remota",
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

    override fun agregarCv(idPostulante: Long, cvPath: String) {
        val postulante = postulanteRepository.findById(idPostulante)
            .orElseThrow { PostulanteNotFoundException() }

        if (postulante.cvEntries.size >= 4) {
            throw CvLimitExceededException()
        }

        val esPrimerCv = postulante.cvEntries.isEmpty()
        if (esPrimerCv) {
            postulante.cvFavorito = cvPath
        }

        postulante.cvEntries.add(CvEntry(cvPath))

        postulanteRepository.save(postulante)
    }

    override fun setearCvFavorito(idPostulante: Long, cvPath: String) {
        val postulante = postulanteRepository.findById(idPostulante)
            .orElseThrow { PostulanteNotFoundException() }

        if (postulante.cvEntries.none { it.cvPath == cvPath }) {
            throw CvNotFoundException()
        }
        postulante.cvFavorito = cvPath
        postulanteRepository.save(postulante)
    }
}