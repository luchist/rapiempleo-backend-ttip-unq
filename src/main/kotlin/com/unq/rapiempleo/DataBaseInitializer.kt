package com.unq.rapiempleo

import com.unq.rapiempleo.model.Curriculum
import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.model.Oferta
import com.unq.rapiempleo.model.Postulante
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.PostulanteRepository
import org.hibernate.internal.util.collections.CollectionHelper.listOf
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataBaseInitializer (
    private val postulanteRepository : PostulanteRepository,
    private val ofertaRepository: OfertaRepository
) {

    @Bean
    fun initializeDatabase() : CommandLineRunner {
        return CommandLineRunner {

            postulanteRepository.deleteAll();
            ofertaRepository.deleteAll();
            postulanteRepository.resetIdPostulante()
            ofertaRepository.resetIdOferta()

            val postulante = Postulante(cv = Curriculum("Leon Kennedy", "29.456.125"))

            val ofertas = listOf(
                        Oferta("Desarrollador Sr Full Stack", "Se busca...", Modalidad.Hibrido, "Abierto", 500000.00),
                        Oferta("Contador Sr", "Se busca perfil...", Modalidad.Presencial, "Finalizado", 450000.00),
                        Oferta("Desarrollador FrontEnd", "Se busca...", Modalidad.Remoto, "Abierto", 480000.00),
                        Oferta("Jefe de cocina", "Nos encontramos...", Modalidad.Presencial, "Urgente", 650000.00),
                        Oferta("Coordinador de Eventos", "Se busca...", Modalidad.Presencial, "Abierto", 500000.00))
            postulanteRepository.save(postulante);
            ofertaRepository.saveAll(ofertas);
        }
    }
}