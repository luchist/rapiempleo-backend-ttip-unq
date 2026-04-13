package com.unq.rapiempleo.service.impl

import com.unq.rapiempleo.model.Curriculum
import com.unq.rapiempleo.model.Postulante
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.PostulanteRepository
import com.unq.rapiempleo.service.PostulanteService
import com.unq.rapiempleo.service.auxiliar.PostulationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class PostulanteServiceImpl (
    private val ofertaRepository: OfertaRepository,
    private val postulanteRepository: PostulanteRepository,
    private val publisher : ApplicationEventPublisher
) : PostulanteService {

    override fun getPostulante(idPostulante: Long) : Postulante {
        return postulanteRepository.findById(idPostulante).orElseThrow { throw NullPointerException() }
    }

    override fun postularEnOferta(idOferta: Long, cv: Curriculum) {
        var ofertaOpt = ofertaRepository.findById(idOferta).orElseThrow{throw RuntimeException()}
        var postulanteop = postulanteRepository.findById(1).orElseThrow { throw NullPointerException() }

        ofertaOpt.postulantes.add(postulanteop)
        postulanteop.postulaciones.add(ofertaOpt)

        ofertaRepository.save(ofertaOpt)
        postulanteRepository.save(postulanteop)

        publisher.publishEvent(
            PostulationEvent(
                ofertaOpt.ofertante,
                ofertaOpt.titulo)
        )
    }
}