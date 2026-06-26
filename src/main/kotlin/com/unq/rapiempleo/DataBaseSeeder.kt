package com.unq.rapiempleo

import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import com.unq.rapiempleo.dto.PostulanteRegistryDTO
import com.unq.rapiempleo.exceptions.OfertanteNotFoundException
import com.unq.rapiempleo.exceptions.PostulanteNotFoundException
import com.unq.rapiempleo.model.CvEntry
import com.unq.rapiempleo.model.EstadoPostulacion
import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.model.Oferta
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.OfertanteRepository
import com.unq.rapiempleo.repository.PostulacionEstadoRepository
import com.unq.rapiempleo.repository.PostulanteRepository
import com.unq.rapiempleo.service.OfertanteService
import com.unq.rapiempleo.service.PostulanteService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Profile(value = ["dev", "local"])
@Component
class DataBaseSeeder(
    private val postulanteRepository: PostulanteRepository,
    private val ofertaRepository: OfertaRepository,
    private val ofertanteRepository: OfertanteRepository,
    private val ofertanteService: OfertanteService,
    private val postulanteService: PostulanteService,
    private val postulacionEstadoRepository: PostulacionEstadoRepository
) {
    private fun String.readClasspathFile(): String =
        object {}.javaClass.getResource("/$this")!!.readText()

    @Transactional
    @Suppress("LongMethod")
    fun seed() {
        postulacionEstadoRepository.deleteAll()
        ofertaRepository.deleteAll()
        postulanteRepository.deleteAll()
        ofertanteRepository.deleteAll()
        postulanteRepository.resetIdPostulante()
        ofertaRepository.resetIdOferta()
        ofertanteRepository.resetIdOfertante()
        postulacionEstadoRepository.resetIdPostulacionEstado()

        ofertanteService.registroOfertante(
            OfertanteRegistryDTO(
                "Albert Wesker", "Electro Smart",
                "wesker8180@gmail.com", "passpass")
        )

        ofertanteService.registroOfertante(
            OfertanteRegistryDTO(
                "Ramon Salazar", "PixelLab",
                "salazar_ram@gmail.com", "wordpass")
        )

        ofertanteService.registroOfertante(
            OfertanteRegistryDTO(
                "Jack Baker", "Tech.Inc",
                "baker_jack7@gmail.com", "wordpass")
        )
        postulanteService.registrarUserPostulante(
            PostulanteRegistryDTO(
                "Leon Kennedy",
                "leon0126@gmail.com",
                "passpass")
        )

        val ofertas = listOf(
            Oferta("Desarrollador Sr Full Stack", "Tech.Inc", "descriptions/FullstackTechOffer.md".readClasspathFile(),
                Modalidad.Hibrido, "Abierto", 45000, 55000, "Lomas de Zamora, Buenos Aires", favorito = false),
            Oferta("Contador Sr", "Tepago SA", "descriptions/ContadorPagoOffer.md".readClasspathFile(),
                Modalidad.Presencial, "Finalizado", 40000, 44000, "Temperley, Buenos Aires", favorito = false),
            Oferta("Desarrollador FrontEnd", "Electro Smart", "descriptions/FrontElectroOffer.md".readClasspathFile(),
                Modalidad.Remoto, "Abierto", 48000, 54000, "Caballito, Buenos Aires", favorito = false),
            Oferta("Jefe de cocina", "Delicatus", "descriptions/JefeCocinaOffer.md".readClasspathFile(),
                Modalidad.Presencial, "Urgente", 65000, 72000, "Recoleta, Buenos Aires", favorito = false),
            Oferta("Organizador de Eventos", "Sweet Retro", "descriptions/OrganizadorSweet.md".readClasspathFile(),
                Modalidad.Presencial, "Abierto", 42000, 48000, "Villa Mercedes, San Luis", favorito = false),
            Oferta("Frontend Developer", "NovaTech", "descriptions/FrontNovatechOffer.md".readClasspathFile(),
                Modalidad.Presencial, "Abierto", 51000, 57000, "Buenos Aires, Argentina", favorito = false),
            Oferta("Backend Engineer", "CloudSync", "descriptions/CloudSyncBackOffer.md".readClasspathFile(),
                Modalidad.Remoto, "Abierto", 47000, 54000, "Ciudad de México, México", favorito = false),
            Oferta("UX Designer", "PixelLab", "descriptions/PixelLabUXOffer.md".readClasspathFile(),
                Modalidad.Hibrido, "Abierto", 52000, 57000, "Buenos Aires, Argentina", favorito = false),
            Oferta("Analista en Marketing", "Onsu", "descriptions/OnsuAnalistaOffer.md".readClasspathFile(),
                Modalidad.Presencial, "Abierto", 35000, 39000, "Paraná, Entre Ríos", favorito = false),
            Oferta("Lider de Automatización y Control", "Holm Argentina",
                "descriptions/AutomatizacionHolmOffer.md".readClasspathFile(),
                Modalidad.Presencial, "Abierto", 57000, 64000, "Mendoza, Argentina", favorito = false),
            Oferta("Cloud Data Engineer", "Mero Marketing", "descriptions/CloudMeroOffer.md".readClasspathFile(),
                Modalidad.Hibrido, "Abierto", 45000, 49000, "Capital Federal, Buenos Aires", favorito = false))

        val ofertanteTest1 = ofertanteRepository.findById(1L)
            .orElseThrow { OfertanteNotFoundException() }

        val ofertanteTest2 = ofertanteRepository.findById(2L)
            .orElseThrow { OfertanteNotFoundException() }

        val ofertanteTest3 = ofertanteRepository.findById(3L)
            .orElseThrow { OfertanteNotFoundException() }

        ofertas[1].ofertante = ofertanteTest1
        ofertas[2].ofertante = ofertanteTest1
        ofertas[3].ofertante = ofertanteTest1
        ofertas[4].ofertante = ofertanteTest1
        ofertas[9].ofertante = ofertanteTest1

        ofertas[6].ofertante = ofertanteTest1
        ofertas[7].ofertante = ofertanteTest2
        ofertas[8].ofertante = ofertanteTest1
        ofertas[10].ofertante = ofertanteTest1

        ofertas[0].ofertante = ofertanteTest3
        ofertas[5].ofertante = ofertanteTest3
        ofertaRepository.saveAll(ofertas)

        val leon = postulanteRepository.findById(1).orElseThrow { PostulanteNotFoundException() }
        leon.fotoPerfil = "postulante/1/foto.jpg"
        leon.cvEntries.add(CvEntry("1/leon-kennedy-cv-english.pdf"))
        leon.cvFavorito = "1/leon-kennedy-cv-english.pdf"
        postulanteRepository.save(leon)

        val wesker = ofertanteRepository.findById(1).orElseThrow { OfertanteNotFoundException() }
        wesker.fotoPerfil = "ofertante/1/foto.jpg"
        ofertanteRepository.save(wesker)

        postulanteService.postularEnOferta(6L, leon.id_postulante!!)
        postulanteService.postularEnOferta(2L, leon.id_postulante!!)

        val estados = postulacionEstadoRepository.findByPostulante(leon)
        estados.find { it.oferta.id_oferta == 6L }!!.let {
            it.estado = EstadoPostulacion.Entrevistando
            postulacionEstadoRepository.save(it)
        }
        estados.find { it.oferta.id_oferta == 2L }!!.let {
            it.estado = EstadoPostulacion.Cerrado
            postulacionEstadoRepository.save(it)
        }
    }
}
