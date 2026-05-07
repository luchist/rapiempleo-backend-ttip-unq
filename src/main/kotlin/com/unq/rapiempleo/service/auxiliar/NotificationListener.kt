package com.unq.rapiempleo.service.auxiliar

import com.unq.rapiempleo.repository.OfertanteRepository
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class NotificationListener(
    private val ofertanteRepository: OfertanteRepository
) {

    @EventListener
    fun onPustulacionRealizada(event: PostulationEvent) {
        val ofertante = ofertanteRepository.findById(event.ofertanteId).orElseThrow { throw NullPointerException("") }

        ofertante.nuevaNotifcacion = true
        ofertante.addNuevaNotificacion(event.ofertaTitulo)
        ofertanteRepository.save(ofertante)
    }
}