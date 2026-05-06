package com.unq.rapiempleo

import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import com.unq.rapiempleo.dto.PostulanteRegistryDTO
import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.model.Oferta
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.OfertanteRepository
import com.unq.rapiempleo.repository.PostulanteRepository
import com.unq.rapiempleo.service.OfertanteService
import com.unq.rapiempleo.service.PostulanteService
import jakarta.transaction.Transactional
import org.hibernate.internal.util.collections.CollectionHelper.listOf
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
    private val postulanteService: PostulanteService
) {

    fun String.readClasspathFile(): String =
        object {}.javaClass
            .getResource("/$this")!!
            .readText()

    @Transactional
    @Bean
    fun initializeDatabase() : CommandLineRunner {
        return CommandLineRunner {

            ofertanteRepository.deleteAll()
            postulanteRepository.deleteAll()
            ofertaRepository.deleteAll()
            postulanteRepository.resetIdPostulante()
            ofertaRepository.resetIdOferta()
            ofertanteRepository.resetIdOfertante()

            ofertanteService.registroOfertante(
                OfertanteRegistryDTO("Albert Wesker", "Electro Smart","wesker8180@gmail.com", "passpass"))
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

            val ofertanteTest = ofertanteRepository.findById(1).orElseThrow { RuntimeException() }
            ofertas[1].ofertante = ofertanteTest
            ofertas[2].ofertante = ofertanteTest
            ofertas[3].ofertante = ofertanteTest
            ofertaRepository.saveAll(ofertas)
            //ofertanteRepository.save(ofertanteTest)
        }
    }
}