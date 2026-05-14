package com.unq.rapiempleo.service.impl

import com.unq.rapiempleo.dto.AvisoPostulanteDTO
import com.unq.rapiempleo.dto.PostulanteDTO
import com.unq.rapiempleo.dto.PostulanteRegistryDTO
import com.unq.rapiempleo.exceptions.OfferNotFoundException
import com.unq.rapiempleo.exceptions.CvLimitExceededException
import com.unq.rapiempleo.exceptions.PostulanteNotFoundException
import com.unq.rapiempleo.model.CvEntry
import com.unq.rapiempleo.model.PostulacionCv
import com.unq.rapiempleo.model.Postulante
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.PostulanteRepository
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

        ofertaOpt.postulantes.add(postulanteop)
        ofertaOpt.cvPostulantes.add(
            PostulacionCv(postulanteop.id_postulante!!,postulanteop.cvEntries[0].cvPath, ofertaOpt.id_oferta!!))
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


    override fun agregarCv(idPostulante: Long, cvPath: String) {
        val postulante = postulanteRepository.findById(idPostulante)
            .orElseThrow { PostulanteNotFoundException() }

        if (postulante.cvEntries.size >= 4) {
            throw CvLimitExceededException()
        }
        postulante.cvEntries.add(CvEntry(cvPath))
        postulanteRepository.save(postulante)
    }

    override fun notificarCvVisto(idsNotificacion: AvisoPostulanteDTO) {
        val ofertaPostulada = ofertaRepository.findById(idsNotificacion.id_oferta)
            .orElseThrow { OfferNotFoundException() }
        val postulanteANotificar = postulanteRepository.findById(idsNotificacion.id_postulante)
            .orElseThrow { PostulanteNotFoundException() }
        val postulacion = ofertaPostulada.cvPostulantes.find { postulacion -> postulacion.id_postulante == idsNotificacion.id_postulante}

        postulacion!!.cvVisto = true
        postulanteANotificar.notificacionesCv.add(ofertaPostulada.titulo)
        ofertaRepository.save(ofertaPostulada)
        postulanteRepository.save(postulanteANotificar)

    }
}