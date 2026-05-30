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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@Transactional
@ActiveProfiles("test")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostulanteServiceTests {

    @Autowired
    private lateinit var postulanteService : PostulanteService
    @Autowired
    private lateinit var postulanteRepository : PostulanteRepository
    @Autowired
    private lateinit var ofertaRepository: OfertaRepository
    @Autowired
    private lateinit var ofertanteService: OfertanteService
    @Autowired
    private lateinit var ofertanteRepository: OfertanteRepository


    @BeforeEach
    fun setupOffersAndUsers() {
        val datosPostulante = PostulanteRegistryDTO("Mock Rodriguez", "mock05@gmail.com", "passpass")
        postulanteService.registrarUserPostulante(datosPostulante)

        val datosOfertante = OfertanteRegistryDTO("Jack Baker", "Tech.Inc", "baker_jack7@gmail.com", "passpass")
        ofertanteService.registroOfertante(datosOfertante)
        val ofertante = ofertanteRepository.findById(1).get()

        val oferta = Oferta("Desarrollador Sr", "Tech.Inc", "descriptions/FullstackTechOffer.md",
            Modalidad.Hibrido, "Abierto", 45000, 55000, "Lomas de Zamora, Buenos Aires", favorito = true)
        oferta.ofertante = ofertante
        ofertaRepository.save(oferta)
    }

    @AfterEach
    fun cleanUp() {
        postulanteRepository.deleteAll()
        postulanteRepository.resetIdPostulante()
        ofertaRepository.deleteAll()
        ofertaRepository.resetIdOferta()
        ofertanteRepository.deleteAll()
        ofertanteRepository.resetIdOfertante()
    }

    @Test
    fun crearPostulante() {
        val datosDeRegistro = PostulanteRegistryDTO("Chris CampoRojo", "christan52@gmail.com", "pass")
        this.postulanteService.registrarUserPostulante(datosDeRegistro)

        val postulanteRegistrado = postulanteService.getPostulante(2)

        Assertions.assertEquals(datosDeRegistro.name, postulanteRegistrado.nombre)
        Assertions.assertEquals(0, postulanteRegistrado.notificacionesCv.size)
        Assertions.assertEquals(0, postulanteRegistrado.ofertasFavoritas.size)
    }

    @Test
    fun obtenerPreferenciasPostulante() {
        val postulanteRegistrado = postulanteService.getPostulante(1)
        val preferencias = postulanteService.getPreferencias(1)
        Assertions.assertEquals("Mock Rodriguez", postulanteRegistrado.nombre)
        Assertions.assertEquals("Estoy buscando trabajo como desarrollador, en la ciudad de Buenos Aires. Prefiero los trabajos con modalidad remota", preferencias)
    }

    @Test
    fun agregarUnCVaPostulante() {
        postulanteService.agregarCv(1, "1/cv_spanish.pdf")

        val postulanteConCV = postulanteService.getPostulante(1)
        Assertions.assertEquals(1, postulanteConCV.cvPaths.size)
        Assertions.assertEquals("1/cv_spanish.pdf", postulanteConCV.cvPaths[0])
    }

    @Test
    fun agregarUnUnicoCVLoSeteaComoFavorito() {
        postulanteService.agregarCv(1, "1//cv_spanish.pdf")

        val postulanteConCV = postulanteService.getPostulante(1)
        Assertions.assertEquals(1, postulanteConCV.cvPaths.size)
        Assertions.assertEquals("1//cv_spanish.pdf", postulanteConCV.cvFavorito)
    }

    @Test
    fun setearSegundoCvComoFavorito() {
        postulanteService.agregarCv(1, "1//cv_spanish.pdf")
        postulanteService.agregarCv(1, "1//cv_english.pdf")

        postulanteService.setearCvFavorito(1, "1//cv_english.pdf")

        val postulante = postulanteService.getPostulante(1)
        Assertions.assertEquals("1//cv_english.pdf", postulante.cvFavorito)
    }

    /*
    @Test
    fun postularAOferta() {
        postulanteService.agregarCv(1, "1//cv_spanish.pdf")

        this.postulanteService.postularEnOferta(1, 1)

        val postulantePostulado = this.postulanteRepository.findById(1).get()
        val ofertaPostulada = this.ofertaRepository.findById(1).get()

        Assertions.assertEquals(1, ofertaPostulada.cvPostulantes.size)
        Assertions.assertEquals("1//cv_spanish.pdf", ofertaPostulada.cvPostulantes[0].cvPathPostulacion)
        Assertions.assertEquals(1, postulantePostulado.postulaciones.size)
        Assertions.assertEquals("Desarrollador Sr", postulantePostulado.postulaciones[0].titulo)
    }
    */



}