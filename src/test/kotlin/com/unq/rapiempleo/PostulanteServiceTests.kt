package com.unq.rapiempleo

import com.unq.rapiempleo.dto.CvEntryRequestDTO
import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import com.unq.rapiempleo.dto.PostulanteRegistryDTO
import com.unq.rapiempleo.exceptions.AccessDeniedToFileException
import com.unq.rapiempleo.exceptions.AccessDeniedToPostulacionException
import com.unq.rapiempleo.exceptions.CvLimitExceededException
import com.unq.rapiempleo.exceptions.CvNotFoundException
import com.unq.rapiempleo.exceptions.DuplicatedEmailException
import com.unq.rapiempleo.exceptions.EstadoSinCambiosException
import com.unq.rapiempleo.exceptions.OfferNotFoundException
import com.unq.rapiempleo.exceptions.PostulacionEstadoNotFoundException
import com.unq.rapiempleo.exceptions.PostulanteNotFoundException
import com.unq.rapiempleo.exceptions.PreferenciaLimitExceededException
import com.unq.rapiempleo.model.EstadoOferta
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
import org.junit.jupiter.api.assertThrows
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
    @Autowired
    private lateinit var postulacionEstadoRepository: PostulacionEstadoRepository


    @BeforeEach
    fun setupOffersAndUsers() {
        val datosPostulante = PostulanteRegistryDTO("Mock Rodriguez", "mock05@gmail.com", "passpass")
        postulanteService.registrarUserPostulante(datosPostulante)

        val datosOfertante = OfertanteRegistryDTO("Jack Baker", "Tech.Inc", "baker_jack7@gmail.com", "passpass")
        ofertanteService.registroOfertante(datosOfertante)
        val ofertante = ofertanteRepository.findById(1).get()

        val oferta = Oferta("Desarrollador Sr", "Tech.Inc", "descriptions/FullstackTechOffer.md",
            Modalidad.Hibrido, EstadoOferta.Abierto, 45000, 55000, "Lomas de Zamora, Buenos Aires")
        oferta.ofertante = ofertante
        ofertaRepository.save(oferta)
    }

    @AfterEach
    fun cleanUp() {
        postulacionEstadoRepository.deleteAll()
        postulacionEstadoRepository.resetIdPostulacionEstado()
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

    @Test
    fun setearCvFavoritoNoAgregadoLanzaExcepcion() {
        postulanteService.agregarCv(1, "1//cv_spanish.pdf")

        assertThrows<CvNotFoundException> {
            postulanteService.setearCvFavorito(1, "1//cv_english.pdf")
        }
    }

    @Test
    fun subirImagenDePerfilPostulante() {
        postulanteService.actualizarImagenPerfil(1, "1//img_profile.jpg")

        val postulante = postulanteService.getPostulante(1)
        Assertions.assertEquals("1//img_profile.jpg", postulante.fotoPerfil)
    }

    @Test
    fun subirSegundaImagenDePerfilPostulanteReemplazaLaAnterior() {
        postulanteService.actualizarImagenPerfil(1, "1//img_profile.jpg")
        postulanteService.actualizarImagenPerfil(1, "1//img_profile2.jpg")

        val postulante = postulanteService.getPostulante(1)
        Assertions.assertEquals("1//img_profile2.jpg", postulante.fotoPerfil)
    }

    @Test
    fun actualizarPreferenciasPostulante() {
        val nuevaPreferencia = "Busco trabajo remoto como desarrollador backend en Argentina."

        postulanteService.actualizarPreferencias(1, nuevaPreferencia)

        val preferencias = postulanteService.getPreferencias(1)
        Assertions.assertEquals(nuevaPreferencia, preferencias)
    }

    @Test
    fun actualizarPreferenciasVariosCambiosConservaElUltimo() {
        postulanteService.actualizarPreferencias(1, "Primera preferencia")
        postulanteService.actualizarPreferencias(1, "Segunda preferencia")

        val preferencias = postulanteService.getPreferencias(1)
        Assertions.assertEquals("Segunda preferencia", preferencias)
    }

    @Test
    fun actualizarPreferenciasPostulanteInexistenteLanzaExcepcion() {
        assertThrows<PostulanteNotFoundException> {
            postulanteService.actualizarPreferencias(999, "Alguna preferencia")
        }
    }

    @Test
    fun actualizarPreferenciasConMasDe255CaracteresLanzaExcepcion() {
        val preferenciaLarga = "a".repeat(256)

        assertThrows<PreferenciaLimitExceededException> {
            postulanteService.actualizarPreferencias(1, preferenciaLarga)
        }
    }

    @Test
    fun actualizarPreferenciasConExactamente255CaracteresEsValido() {
        val preferencia255 = "a".repeat(255)

        postulanteService.actualizarPreferencias(1, preferencia255)

        Assertions.assertEquals(preferencia255, postulanteService.getPreferencias(1))
    }

    @Test
    fun obtenerIdPostulantePorEmail() {
        val postulanteId = postulanteService.getIdPorEmail("mock05@gmail.com")
        Assertions.assertEquals(1, postulanteId)
    }

    @Test
    fun agregarUnaOfertaFavorita() {
        postulanteService.agregarOfertaFavorita(1, 1)
        val postulanteConFavorito = postulanteRepository.findById(1).get()

        Assertions.assertEquals(1, postulanteConFavorito.favoritos.size)
        Assertions.assertEquals("Desarrollador Sr", postulanteConFavorito.favoritos[0].titulo)
        Assertions.assertEquals("Tech.Inc", postulanteConFavorito.favoritos[0].empresa)
    }

    @Test
    fun removerFavoritoAUnaOfertaFavorita() {
        postulanteService.agregarOfertaFavorita(1, 1)
        postulanteService.removerOfertaFavorita(1, 1)
        val postulanteConFavorito = postulanteRepository.findById(1).get()

        Assertions.assertEquals(0, postulanteConFavorito.favoritos.size)
    }

    @Test
    fun excepcionRemoverOfertaInexistenteDeFavoritos() {
        Assertions.assertThrows(OfferNotFoundException::class.java) {
            postulanteService.removerOfertaFavorita(1, 99)
        }
    }

    @Test
    fun excepcionRemoverFavoritoComoPostulanteInexistente() {
        Assertions.assertThrows(PostulanteNotFoundException::class.java) {
            postulanteService.removerOfertaFavorita(99, 1)
        }
    }

    @Test
    fun removerUnicoCVDelPerfil() {
        postulanteService.agregarCv(1, "1/cv_spanish.pdf")
        val requestRemoveCV = CvEntryRequestDTO(1, "1/cv_spanish.pdf")

        postulanteService.removerCvIndicado(requestRemoveCV)
        val postulanteSinCV = postulanteService.getPostulante(1)
        Assertions.assertEquals(0, postulanteSinCV.cvPaths.size)
        Assertions.assertNull(postulanteSinCV.cvFavorito)
    }

    @Test
    fun removerCVFavoritoConMasDeUnCVSubido() {
        postulanteService.agregarCv(1, "1/cv_spanish.pdf")
        postulanteService.agregarCv(1, "1/cv_english.pdf")
        postulanteService.setearCvFavorito(1, "1/cv_english.pdf")

        val requestRemoveCV = CvEntryRequestDTO(1, "1/cv_english.pdf")
        postulanteService.removerCvIndicado(requestRemoveCV)
        val postulanteConCV = postulanteService.getPostulante(1)

        Assertions.assertEquals(1, postulanteConCV.cvPaths.size)
        Assertions.assertEquals("1/cv_spanish.pdf", postulanteConCV.cvPaths[0])
        Assertions.assertEquals("1/cv_spanish.pdf", postulanteConCV.cvFavorito)
    }

    @Test
    fun excepcionPostulanteInexistenteEliminaUnCV() {
        val requestRemoveCV = CvEntryRequestDTO(99, "1/cv_english.pdf")
        Assertions.assertThrows(PostulanteNotFoundException::class.java) {
            postulanteService.removerCvIndicado(requestRemoveCV)
        }
    }

    @Test
    fun excepcionSeIntentaEliminarUnCVInexistente() {
        postulanteService.agregarCv(1, "1/cv_spanish.pdf")
        val requestRemoveCV = CvEntryRequestDTO(1, "1/cv_japanese.pdf")

        Assertions.assertThrows(CvNotFoundException::class.java) {
            postulanteService.removerCvIndicado(requestRemoveCV)
        }
    }

    @Test
    fun excepcionAlObtenerBoardDeUsuarioInexistente() {
        Assertions.assertThrows(PostulanteNotFoundException::class.java) {
            postulanteService.getBoard(99)
        }
    }

    @Test
    fun excepcionRegistrarPostulanteConEmailDuplicado() {
        val datosDuplicados = PostulanteRegistryDTO("Otro Mock", "mock05@gmail.com", "otrapass")
        assertThrows<DuplicatedEmailException> {
            postulanteService.registrarUserPostulante(datosDuplicados)
        }
    }

    @Test
    fun excepcionGetPostulanteInexistente() {
        assertThrows<PostulanteNotFoundException> {
            postulanteService.getPostulante(999)
        }
    }

    @Test
    fun excepcionGetPreferenciasDePostulanteInexistente() {
        assertThrows<PostulanteNotFoundException> {
            postulanteService.getPreferencias(999)
        }
    }

    @Test
    fun excepcionGetIdPorEmailNoRegistrado() {
        assertThrows<PostulanteNotFoundException> {
            postulanteService.getIdPorEmail("noexiste@gmail.com")
        }
    }

    @Test
    fun excepcionAgregarCvConPathDeOtroPostulante() {
        assertThrows<AccessDeniedToFileException> {
            postulanteService.agregarCv(1, "99/cv_spanish.pdf")
        }
    }

    @Test
    fun excepcionAgregarQuintoCvSuperaElLimite() {
        postulanteService.agregarCv(1, "1/cv1.pdf")
        postulanteService.agregarCv(1, "1/cv2.pdf")
        postulanteService.agregarCv(1, "1/cv3.pdf")
        postulanteService.agregarCv(1, "1/cv4.pdf")

        assertThrows<CvLimitExceededException> {
            postulanteService.agregarCv(1, "1/cv5.pdf")
        }
    }

    @Test
    fun excepcionSetearCvFavoritoConPathDeOtroPostulante() {
        assertThrows<AccessDeniedToFileException> {
            postulanteService.setearCvFavorito(1, "99/cv_spanish.pdf")
        }
    }

    @Test
    fun excepcionAgregarOfertaFavoritaInexistente() {
        assertThrows<OfferNotFoundException> {
            postulanteService.agregarOfertaFavorita(1, 999)
        }
    }

    @Test
    fun excepcionAgregarOfertaFavoritaComoPostulanteInexistente() {
        assertThrows<PostulanteNotFoundException> {
            postulanteService.agregarOfertaFavorita(999, 1)
        }
    }

    @Test
    fun excepcionActualizarImagenPerfilDePostulanteInexistente() {
        assertThrows<PostulanteNotFoundException> {
            postulanteService.actualizarImagenPerfil(999, "1//img_profile.jpg")
        }
    }

    @Test
    fun updateEstadoDeUnaPostulacionAjenaLanzaExcepcion() {
        val idEstado = crearPostulacionEstado(EstadoPostulacion.Aplicado)

        assertThrows<AccessDeniedToPostulacionException> {
            postulanteService.updateEstadoPostulacion(2, idEstado, EstadoPostulacion.Entrevistando)
        }
    }

    @Test
    fun updateEstadoPostulacionInexistenteLanzaExcepcion() {
        assertThrows<PostulacionEstadoNotFoundException> {
            postulanteService.updateEstadoPostulacion(1, 9999, EstadoPostulacion.Entrevistando)
        }
    }

    @Test
    fun updateEstadoPostulacionCambiaElEstado() {
        val idEstado = crearPostulacionEstado(EstadoPostulacion.Aplicado)

        postulanteService.updateEstadoPostulacion(1, idEstado, EstadoPostulacion.Entrevistando)

        val actualizado = postulacionEstadoRepository.findById(idEstado).get()
        Assertions.assertEquals(EstadoPostulacion.Entrevistando, actualizado.estado)
    }

    @Test
    fun updateEstadoPostulacionAlMismoEstadoLanzaExcepcion() {
        val idEstado = crearPostulacionEstado(EstadoPostulacion.Aplicado)

        assertThrows<EstadoSinCambiosException> {
            postulanteService.updateEstadoPostulacion(1, idEstado, EstadoPostulacion.Aplicado)
        }
    }

    private fun crearPostulacionEstado(estado: EstadoPostulacion): Long {
        val postulante = postulanteRepository.findById(1).get()
        val oferta = ofertaRepository.findById(1).get()
        val postulacionEstado = postulacionEstadoRepository.save(
            PostulacionEstado(oferta, postulante, estado)
        )
        return postulacionEstado.id_postulacion_estado!!
    }

}