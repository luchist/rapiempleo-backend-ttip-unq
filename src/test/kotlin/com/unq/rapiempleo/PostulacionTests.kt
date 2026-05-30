package com.unq.rapiempleo

import com.unq.rapiempleo.dto.AvisoPostulanteDTO
import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import com.unq.rapiempleo.dto.PostulanteRegistryDTO
import com.unq.rapiempleo.model.EstadoCvPostulado
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean


@Transactional
@ActiveProfiles("test")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostulacionTests {

    @Autowired
    private lateinit var postulanteRepository : PostulanteRepository
    @Autowired
    private lateinit var ofertaRepository: OfertaRepository
    @Autowired
    private lateinit var ofertanteService: OfertanteService
    @Autowired
    private lateinit var ofertanteRepository: OfertanteRepository
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder
    @Autowired
    private lateinit var publisher : ApplicationEventPublisher
    @MockitoBean
    private var  postulacionEstadoRepository: PostulacionEstadoRepository = mock()
    @Autowired
    private lateinit var postulanteService: PostulanteService


    @BeforeEach
    fun setUp() {
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
    fun tearDown() {
        postulanteRepository.deleteAll()
        postulanteRepository.resetIdPostulante()
        ofertaRepository.deleteAll()
        ofertaRepository.resetIdOferta()
        ofertanteRepository.deleteAll()
        ofertanteRepository.resetIdOfertante()
    }

    @Test
    fun postularAOferta() {
        postulanteService.agregarCv(1, "1//cv_spanish.pdf")

        val oferta = ofertaRepository.findById(1).get()
        val postulante = postulanteRepository.findById(1).get()
        val estadoMock = PostulacionEstado(oferta, postulante, EstadoPostulacion.Aplicado)

        whenever(postulacionEstadoRepository.save(any())).doReturn(estadoMock)
        this.postulanteService.postularEnOferta(1, 1)

        val postulantePostulado = this.postulanteRepository.findById(1).get()
        val ofertaPostulada = this.ofertaRepository.findById(1).get()

        verify(postulacionEstadoRepository, times(1)).save(any())
        Assertions.assertEquals(1, ofertaPostulada.cvPostulantes.size)
        Assertions.assertEquals("1//cv_spanish.pdf", ofertaPostulada.cvPostulantes[0].cvPathPostulacion)
        Assertions.assertEquals(1, postulantePostulado.postulaciones.size)
        Assertions.assertEquals("Desarrollador Sr", postulantePostulado.postulaciones[0].titulo)
    }

    @Test
    fun ofertanteRecibeNotificacionDePostulacion() {
        postulanteService.agregarCv(1, "1//cv_spanish.pdf")

        val oferta = ofertaRepository.findById(1).get()
        val postulante = postulanteRepository.findById(1).get()
        val estadoMock = PostulacionEstado(oferta, postulante, EstadoPostulacion.Aplicado)

        whenever(postulacionEstadoRepository.save(any())).doReturn(estadoMock)
        this.postulanteService.postularEnOferta(1, 1)

        verify(postulacionEstadoRepository).save(any())

        val ofertante = ofertanteService.recuperarOfertante(1)
        Assertions.assertEquals(1, ofertante.cantidadNotifacion)
        Assertions.assertEquals("Desarrollador Sr", ofertante.avisosPostulacion[0])
    }

    @Test
    fun ofertanteEliminaNotificacionRecibida() {
        postulanteService.agregarCv(1, "1//cv_spanish.pdf")

        val oferta = ofertaRepository.findById(1).get()
        val postulante = postulanteRepository.findById(1).get()
        val estadoMock = PostulacionEstado(oferta, postulante, EstadoPostulacion.Aplicado)

        whenever(postulacionEstadoRepository.save(any())).doReturn(estadoMock)
        this.postulanteService.postularEnOferta(1, 1)

        verify(postulacionEstadoRepository).save(any())

        ofertanteService.eliminarNotificacion(1,0)
        val ofertante = ofertanteService.recuperarOfertante(1)

        Assertions.assertEquals(0, ofertante.cantidadNotifacion)
    }

    @Test
    fun postulanteRecibeNtotificacionTrasAcciondeOfertante() {
        postulanteService.agregarCv(1, "1//cv_spanish.pdf")

        val oferta = ofertaRepository.findById(1).get()
        val postulante = postulanteRepository.findById(1).get()
        val estadoMock = PostulacionEstado(oferta, postulante, EstadoPostulacion.Aplicado)

        whenever(postulacionEstadoRepository.save(any())).doReturn(estadoMock)
        this.postulanteService.postularEnOferta(1, 1)

        verify(postulacionEstadoRepository).save(any())

        val avisoEnCV = AvisoPostulanteDTO(1,1, EstadoCvPostulado.VISTO)
        postulanteService.notificarAccionEnCv(avisoEnCV)

        val postulanteNotificado = postulanteRepository.findById(1).get()

        Assertions.assertEquals(1, postulanteNotificado.notificacionesCv.size)
        Assertions.assertEquals("Desarrollador Sr", postulanteNotificado.notificacionesCv[0].titleNotif)
    }

    @Test
    fun postulanteEliminaLaNotificacionRecibida() {
        postulanteService.agregarCv(1, "1//cv_spanish.pdf")

        val oferta = ofertaRepository.findById(1).get()
        val postulante = postulanteRepository.findById(1).get()
        val estadoMock = PostulacionEstado(oferta, postulante, EstadoPostulacion.Aplicado)

        whenever(postulacionEstadoRepository.save(any())).doReturn(estadoMock)
        this.postulanteService.postularEnOferta(1, 1)

        verify(postulacionEstadoRepository).save(any())

        val avisoEnCV = AvisoPostulanteDTO(1,1, EstadoCvPostulado.VISTO)
        postulanteService.notificarAccionEnCv(avisoEnCV)

        postulanteService.eliminarNotificacion(1, 0)

        val postulanteNotificado = postulanteRepository.findById(1).get()

        Assertions.assertEquals(0, postulanteNotificado.notificacionesCv.size)
    }
}

