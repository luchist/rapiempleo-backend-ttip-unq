package com.unq.rapiempleo

import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import com.unq.rapiempleo.dto.PostulanteRegistryDTO
import com.unq.rapiempleo.model.EstadoPostulacion
import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.model.Oferta
import com.unq.rapiempleo.model.PostulacionEstado
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.OfertanteRepository
import com.unq.rapiempleo.repository.PostulacionEstadoRepository
import com.unq.rapiempleo.repository.PostulanteRepository
import com.unq.rapiempleo.service.OfertanteService
import com.unq.rapiempleo.service.PostulanteService
import jakarta.transaction.Transactional
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Transactional
@Configuration
class DataBaseInitializer (
    private val postulanteRepository : PostulanteRepository,
    private val ofertaRepository: OfertaRepository,
    private val ofertanteRepository: OfertanteRepository,
    private val ofertanteService: OfertanteService,
    private val postulanteService: PostulanteService,
    private val postulacionEstadoRepository: PostulacionEstadoRepository
) {

    fun String.readClasspathFile(): String =
        object {}.javaClass
            .getResource("/$this")!!
            .readText()

    @Transactional
    @Bean
    fun initializeDatabase() : CommandLineRunner {
        return CommandLineRunner {

            postulacionEstadoRepository.deleteAll()
            ofertanteRepository.deleteAll()
            postulanteRepository.deleteAll()
            ofertaRepository.deleteAll()
            postulanteRepository.resetIdPostulante()
            ofertaRepository.resetIdOferta()
            ofertanteRepository.resetIdOfertante()
            postulacionEstadoRepository.resetIdPostulacionEstado()

            ofertanteService.registroOfertante(
                OfertanteRegistryDTO("Albert Wesker", "Electro Smart","wesker8180@gmail.com", "passpass"))
            ofertanteService.registroOfertante(
                OfertanteRegistryDTO("Ramon Salazar", "PixelLab", "salazar_ram@gmail.com", "wordpass"))
            ofertanteService.registroOfertante(
                OfertanteRegistryDTO("Jack Baker", "Tech.Inc", "baker_jack7@gmail.com", "wordpass")
            )
            postulanteService.registrarUserPostulante(
                PostulanteRegistryDTO("Leon Kennedy", "leon0126@gmail.com", "passpass"))

            val ofertas = listOf(
                        Oferta("Desarrollador Sr Full Stack", "Tech.Inc", "descriptions/FullstackTechOffer.md".readClasspathFile(),
                            Modalidad.Hibrido, "Abierto", 45000, 55000, "Lomas de Zamora, Buenos Aires", favorito = true),
                        Oferta("Contador Sr","Tepago SA", "descriptions/ContadorPagoOffer.md".readClasspathFile(),
                            Modalidad.Presencial, "Finalizado", 40000, 44000, "Temperley, Buenos Aires", favorito = false),
                        Oferta("Desarrollador FrontEnd","Electro Smart", "descriptions/FrontElectroOffer.md".readClasspathFile(),
                            Modalidad.Remoto, "Abierto", 48000, 54000, "Caballito, Buenos Aires", favorito = false),
                        Oferta("Jefe de cocina", "Delicatus", "descriptions/JefeCocinaOffer.md".readClasspathFile(),
                            Modalidad.Presencial, "Urgente", 65000, 72000, "Recoleta, Buenos Aires", favorito = false),
                        Oferta("Organizador de Eventos","Sweet Retro", "descriptions/OrganizadorSweet.md".readClasspathFile(),
                            Modalidad.Presencial, "Abierto", 42000, 48000, "Villa Mercedes, San Luis", favorito = false),
                        Oferta("Frontend Developer","NovaTech", "descriptions/FrontNovatechOffer.md".readClasspathFile(),
                            Modalidad.Presencial, "Abierto", 51000, 57000, "Buenos Aires, Argentina", favorito = true),
                        Oferta("Backend Engineer","CloudSync", "descriptions/CloudSyncBackOffer.md".readClasspathFile(),
                            Modalidad.Remoto, "Abierto", 47000, 54000, "Ciudad de México, México", favorito = true),
                        Oferta("UX Designer","PixelLab", "descriptions/PixelLabUXOffer.md".readClasspathFile(),
                            Modalidad.Hibrido, "Abierto", 52000, 57000, "Buenos Aires, Argentina", favorito = false),
                        Oferta("Analista en Marketing","Onsu", "descriptions/OnsuAnalistaOffer.md".readClasspathFile(),
                            Modalidad.Presencial, "Abierto", 35000, 39000, "Paraná, Entre Ríos", favorito = false),
                        Oferta("Lider de Automatización y Control","Holm Argentina", "descriptions/AutomatizacionHolmOffer.md".readClasspathFile(),
                            Modalidad.Presencial, "Abierto", 57000, 64000, "Mendoza, Argentina", favorito = false),
                        Oferta("Cloud Data Engineer","Mero Marketing", "descriptions/CloudMeroOffer.md".readClasspathFile(),
                            Modalidad.Hibrido, "Abierto", 45000, 49000, "Capital Federal, Buenos Aires", favorito = true))

            val ofertanteTest1 = ofertanteRepository.findById(1).orElseThrow { RuntimeException() }
            val ofertanteTest2 = ofertanteRepository.findById(2).orElseThrow { RuntimeException() }
            val ofertanteTest3 = ofertanteRepository.findById(3).orElseThrow { RuntimeException() }
            ofertas[1].ofertante = ofertanteTest1
            ofertas[2].ofertante = ofertanteTest1
            ofertas[3].ofertante = ofertanteTest1
            ofertas[4].ofertante = ofertanteTest1
            ofertas[9].ofertante = ofertanteTest1

            ofertas[6].ofertante = ofertanteTest2
            ofertas[7].ofertante = ofertanteTest2
            ofertas[8].ofertante = ofertanteTest2
            ofertas[10].ofertante = ofertanteTest2

            ofertas[0].ofertante = ofertanteTest3
            ofertas[5].ofertante = ofertanteTest3
            ofertaRepository.saveAll(ofertas)


            val leon = postulanteRepository.findById(1).orElseThrow { RuntimeException() }

            // for status board
            val ofertaEntrevistando = ofertaRepository.findById(6).orElseThrow { RuntimeException() }
            postulacionEstadoRepository.save(
                PostulacionEstado(oferta = ofertaEntrevistando, postulante = leon, estado = EstadoPostulacion.Entrevistando)
            )

            val ofertaCerrada = ofertaRepository.findById(2).orElseThrow { RuntimeException() }
            postulacionEstadoRepository.save(
                PostulacionEstado(oferta = ofertaCerrada, postulante = leon, estado = EstadoPostulacion.Cerrado)
            )
        }
    }
}
