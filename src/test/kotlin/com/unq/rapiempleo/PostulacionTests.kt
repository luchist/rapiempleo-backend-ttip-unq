package com.unq.rapiempleo

import com.unq.rapiempleo.dto.AvisoPostulanteDTO
import com.unq.rapiempleo.dto.CvCollectRequestDTO
import com.unq.rapiempleo.dto.DeleteCVRequestDTO
import com.unq.rapiempleo.dto.OfertaCreadaDTO
import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import com.unq.rapiempleo.dto.PostulanteRegistryDTO
import com.unq.rapiempleo.exceptions.DuplicatedCVSavedException
import com.unq.rapiempleo.exceptions.OfertanteNotFoundException
import com.unq.rapiempleo.model.EstadoCvPostulado
import com.unq.rapiempleo.model.EstadoPostulacion
import com.unq.rapiempleo.model.EstadoOferta
import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.model.Oferta
import com.unq.rapiempleo.model.PostulacionEstado
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.OfertanteRepository
import com.unq.rapiempleo.repository.PostulacionEstadoRepository
import com.unq.rapiempleo.repository.PostulanteRepository
import com.unq.rapiempleo.service.OfertaService
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
    private lateinit var ofertaService : OfertaService
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
            Modalidad.Hibrido, EstadoOferta.Abierto, 45000, 55000, "Lomas de Zamora, Buenos Aires", favorito = true)
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
    fun excepcionOfertanteInexistenteEliminaNotificacion() {
        Assertions.assertThrows(OfertanteNotFoundException::class.java) {
            ofertanteService.eliminarNotificacion(99, 0)
        }
    }

    @Test
    fun postulanteRecibeNtotificacionTrasOfertanteAbriendoCV() {
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
        Assertions.assertEquals(EstadoCvPostulado.VISTO, postulanteNotificado.notificacionesCv[0].typeNotif)
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

    @Test
    fun postulanteRecibeNtotificacionTrasAccionesDeOfertanteEnCV() {
        postulanteService.agregarCv(1, "1//cv_spanish.pdf")

        val oferta = ofertaRepository.findById(1).get()
        val postulante = postulanteRepository.findById(1).get()
        val estadoMock = PostulacionEstado(oferta, postulante, EstadoPostulacion.Aplicado)

        whenever(postulacionEstadoRepository.save(any())).doReturn(estadoMock)
        this.postulanteService.postularEnOferta(1, 1)

        verify(postulacionEstadoRepository).save(any())

        val avisoEnCV1 = AvisoPostulanteDTO(1,1, EstadoCvPostulado.VISTO)
        val avisoEnCV2 = AvisoPostulanteDTO(1,1, EstadoCvPostulado.CONSIDERACION)
        postulanteService.notificarAccionEnCv(avisoEnCV1)
        postulanteService.notificarAccionEnCv(avisoEnCV2)

        val postulanteNotificado = postulanteRepository.findById(1).get()

        Assertions.assertEquals(2, postulanteNotificado.notificacionesCv.size)
        Assertions.assertEquals("Desarrollador Sr", postulanteNotificado.notificacionesCv[0].titleNotif)
        Assertions.assertEquals(EstadoCvPostulado.VISTO, postulanteNotificado.notificacionesCv[0].typeNotif)
        Assertions.assertEquals("Desarrollador Sr", postulanteNotificado.notificacionesCv[1].titleNotif)
        Assertions.assertEquals(EstadoCvPostulado.CONSIDERACION, postulanteNotificado.notificacionesCv[1].typeNotif)
    }

    @Test
    fun ofertanteCambiaDeSectorElCVRecibidoTrasAccionDeRechazar() {
        postulanteService.agregarCv(1, "1//cv_spanish.pdf")

        val oferta = ofertaRepository.findById(1).get()
        val postulante = postulanteRepository.findById(1).get()
        val estadoMock = PostulacionEstado(oferta, postulante, EstadoPostulacion.Aplicado)

        whenever(postulacionEstadoRepository.save(any())).doReturn(estadoMock)
        this.postulanteService.postularEnOferta(1, 1)
        verify(postulacionEstadoRepository).save(any())

        //val avisoEnCV1 = AvisoPostulanteDTO(1,1, EstadoCvPostulado.VISTO)
        val avisoEnCV2 = AvisoPostulanteDTO(1,1, EstadoCvPostulado.CONSIDERACION)
        //postulanteService.notificarAccionEnCv(avisoEnCV1)
        postulanteService.notificarAccionEnCv(avisoEnCV2)

        val ofertaConCVs = OfertaCreadaDTO.desdeModelo(ofertaRepository.findById(1).get())

        Assertions.assertEquals(EstadoCvPostulado.CONSIDERACION, ofertaConCVs.cvsRevisados[0].estadoCv)
        Assertions.assertEquals(0, ofertaConCVs.cvsRecibidos.size)
        Assertions.assertEquals(1, ofertaConCVs.cvsRevisados.size)
        Assertions.assertEquals("1//cv_spanish.pdf", ofertaConCVs.cvsRevisados[0].cvPathPostulacion)
        Assertions.assertEquals(EstadoCvPostulado.CONSIDERACION, ofertaConCVs.cvsRevisados[0].estadoCv)
    }

    @Test
    fun ofertanteGuardaCVRecibidoDePostulacion() {
        postulanteService.agregarCv(1, "1//cv_spanish.pdf")

        val oferta = ofertaRepository.findById(1).get()
        val postulante = postulanteRepository.findById(1).get()
        val estadoMock = PostulacionEstado(oferta, postulante, EstadoPostulacion.Aplicado)

        whenever(postulacionEstadoRepository.save(any())).doReturn(estadoMock)
        this.postulanteService.postularEnOferta(1, 1)
        verify(postulacionEstadoRepository).save(any())

        val cvSaveRequest = CvCollectRequestDTO(1,1, "1//cv_spanish.pdf")
        ofertanteService.guardarCV(cvSaveRequest)

        val ofertante = ofertanteRepository.findById(1).get()

        Assertions.assertEquals(1, ofertante.cvsGuardados.size)
        Assertions.assertEquals("1//cv_spanish.pdf", ofertante.cvsGuardados[0].cvPath)
        Assertions.assertEquals(1, ofertante.cvsGuardados[0].id_postulante)
    }

    @Test
    fun excepcionOfertanteGuardaCVDuplicado() {
        postulanteService.agregarCv(1, "1//cv_spanish.pdf")

        val oferta = ofertaRepository.findById(1).get()
        val postulante = postulanteRepository.findById(1).get()
        val estadoMock = PostulacionEstado(oferta, postulante, EstadoPostulacion.Aplicado)

        whenever(postulacionEstadoRepository.save(any())).doReturn(estadoMock)
        this.postulanteService.postularEnOferta(1, 1)
        verify(postulacionEstadoRepository).save(any())

        val cvSaveRequest = CvCollectRequestDTO(1,1, "1//cv_spanish.pdf")
        ofertanteService.guardarCV(cvSaveRequest)

        Assertions.assertThrows(DuplicatedCVSavedException::class.java) {
            ofertanteService.guardarCV(cvSaveRequest)
        }
    }

    @Test
    fun ofertanteEliminaCvGuardado() {
        postulanteService.agregarCv(1, "1//cv_spanish.pdf")

        val oferta = ofertaRepository.findById(1).get()
        val postulante = postulanteRepository.findById(1).get()
        val estadoMock = PostulacionEstado(oferta, postulante, EstadoPostulacion.Aplicado)

        whenever(postulacionEstadoRepository.save(any())).doReturn(estadoMock)
        this.postulanteService.postularEnOferta(1, 1)
        verify(postulacionEstadoRepository).save(any())

        val cvActionRequest = CvCollectRequestDTO(1,1, "1//cv_spanish.pdf")
        ofertanteService.guardarCV(cvActionRequest)
        ofertanteService.eliminarCVGuardado(cvActionRequest)
        val ofertante = ofertanteRepository.findById(1).get()

        Assertions.assertEquals(0, ofertante.cvsGuardados.size)
    }

    @Test
    fun seEliminaCVdePostulacionEnOferta() {
        postulanteService.agregarCv(1, "1//cv_spanish.pdf")

        val oferta = ofertaRepository.findById(1).get()
        val postulante = postulanteRepository.findById(1).get()
        val estadoMock = PostulacionEstado(oferta, postulante, EstadoPostulacion.Aplicado)

        whenever(postulacionEstadoRepository.save(any())).doReturn(estadoMock)
        this.postulanteService.postularEnOferta(1, 1)
        verify(postulacionEstadoRepository).save(any())

        val deleteCVRequest = DeleteCVRequestDTO(1,1)
        ofertaService.eliminarCVPostulacion(deleteCVRequest)
        val ofertante = ofertanteRepository.findById(1).get()

        Assertions.assertEquals("Desarrollador Sr", ofertante.avisosPostulacion[0])
        Assertions.assertEquals(0, ofertante.ofertasCreadas.size)
    }

}

