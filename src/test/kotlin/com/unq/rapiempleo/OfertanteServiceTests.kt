package com.unq.rapiempleo

import com.unq.rapiempleo.dto.OfertaCreadaDTO
import com.unq.rapiempleo.dto.OfertaCreateRequest
import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import com.unq.rapiempleo.dto.UsuarioLoginDTO
import com.unq.rapiempleo.exceptions.AccessDeniedToFileException
import com.unq.rapiempleo.exceptions.DuplicatedEmailException
import com.unq.rapiempleo.exceptions.OfertanteNotFoundException
import com.unq.rapiempleo.exceptions.OfferNotFoundException
import com.unq.rapiempleo.exceptions.UnauthenticatedException
import com.unq.rapiempleo.model.EstadoOferta
import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.OfertanteRepository
import com.unq.rapiempleo.service.LoginService
import com.unq.rapiempleo.service.OfertanteService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@ActiveProfiles("test")
@SpringBootTest
class OfertanteServiceTests {

    @Autowired
    private lateinit var ofertanteService: OfertanteService
    @Autowired
    private lateinit var ofertanteRepository: OfertanteRepository
    @Autowired
    private lateinit var ofertaRepository: OfertaRepository
    @Autowired
    private lateinit var loginService : LoginService

    @AfterEach
    fun cleanUp() {
        ofertaRepository.deleteAll()
        ofertaRepository.resetIdOferta()
        ofertanteRepository.deleteAll()
        ofertanteRepository.resetIdOfertante()
    }

    private fun crearOfertanteConOferta(): OfertaCreadaDTO {
        ofertanteService.registroOfertante(
            OfertanteRegistryDTO("Mock", "Hyper-MegaRed", "marco@gmail.com", "pass")
        )
        return ofertanteService.crearOferta(
            1, OfertaCreateRequest(
                titulo = "Desarrollador Full Stack",
                descripcion = "Descripción del puesto",
                modalidad = Modalidad.Remoto,
                sueldoMin = 1000,
                sueldoMax = 2000,
                ubicacion = "CABA"
            )
        )
    }

    @Test
    fun crearOfertante() {
        val datosDeRegistro = OfertanteRegistryDTO("Mock", "Hyper-MegaRed", "marco@gmail.com", "pass")
        ofertanteService.registroOfertante(datosDeRegistro)

        val ofertanteRegistrado = ofertanteService.recuperarOfertante(1)

        Assertions.assertEquals(datosDeRegistro.name, ofertanteRegistrado.nombre)
        Assertions.assertEquals(datosDeRegistro.company, ofertanteRegistrado.empresa)
        Assertions.assertEquals(0, ofertanteRegistrado.avisosPostulacion.size)
        Assertions.assertEquals(0, ofertanteRegistrado.ofertasCreadas.size)
    }

    @Test
    fun excepcionAlCrearOfertanteConMailRepetido() {
        val datosRegistro = OfertanteRegistryDTO("Mock", "MegaRed", "marco@gmail.com", "pass")
        ofertanteService.registroOfertante(datosRegistro)
        val datosRegistroDuplicado = OfertanteRegistryDTO("Copy", "RedMega", "marco@gmail.com", "pass")

        Assertions.assertThrows(DuplicatedEmailException::class.java) {
            ofertanteService.registroOfertante(datosRegistroDuplicado)
        }
    }

    @Test
    fun loginOfertante() {
        val datosDeRegistro = OfertanteRegistryDTO("Mock", "RedMega", "marco@gmail.com", "pass")
        ofertanteService.registroOfertante(datosDeRegistro)

        val userLogueado = loginService.loginDeUser(UsuarioLoginDTO("marco@gmail.com", "pass"))

        Assertions.assertEquals(1, userLogueado.id)
        Assertions.assertEquals("Mock", userLogueado.nombre)
        Assertions.assertTrue(userLogueado.token.isNotEmpty())
    }

    @Test
    fun excepcionCambiarFotoDeOfertanteSinLoguear() {
        val authentication = mock(Authentication::class.java)
        Mockito.`when`(authentication.name).thenReturn(null)
        val securityContext = mock(SecurityContext::class.java)
        Mockito.`when`(securityContext.authentication).thenReturn(authentication)
        SecurityContextHolder.setContext(securityContext)

        val datosDeRegistro = OfertanteRegistryDTO("Mock", "RedMega", "mark@gmail.com", "pass")
        ofertanteService.registroOfertante(datosDeRegistro)

        Assertions.assertThrows(UnauthenticatedException::class.java) {
            ofertanteService.actualizarImagenPerfil(1, "1//foto.jpg")
        }
    }

    @Test
    fun excepcionCambiarFotoDeOfertanteDistintoAlLogueado() {
        val authentication = mock(Authentication::class.java)
        Mockito.`when`(authentication.name).thenReturn("mock@gmail.com")
        val securityContext = mock(SecurityContext::class.java)
        Mockito.`when`(securityContext.authentication).thenReturn(authentication)

        SecurityContextHolder.setContext(securityContext)

        val datosDeRegistro = OfertanteRegistryDTO("Mock", "RedMega", "mock@gmail.com", "pass")
        ofertanteService.registroOfertante(datosDeRegistro)

        Assertions.assertThrows(AccessDeniedToFileException::class.java) {
            ofertanteService.actualizarImagenPerfil(99, "ofertante/1/foto.jpg")
        }
    }

    @Test
    fun cambiarFotoDePerfilOfertante() {
        val authentication = mock(Authentication::class.java)
        Mockito.`when`(authentication.name).thenReturn("mock@gmail.com")
        val securityContext = mock(SecurityContext::class.java)
        Mockito.`when`(securityContext.authentication).thenReturn(authentication)

        SecurityContextHolder.setContext(securityContext)

        val datosDeRegistro = OfertanteRegistryDTO("Mock", "RedMega", "mock@gmail.com", "pass")
        ofertanteService.registroOfertante(datosDeRegistro)
        ofertanteService.actualizarImagenPerfil(1, "ofertante/1/foto.jpg")

        val ofertanteConFoto = ofertanteRepository.findById(1).get()

        Assertions.assertEquals("ofertante/1/foto.jpg", ofertanteConFoto.fotoPerfil)
    }

    @Test
    fun obtenerIdDeOfertanteDesdeEmail() {
        val datosDeRegistro = OfertanteRegistryDTO("Mock", "RedMega", "mock@gmail.com", "pass")
        ofertanteService.registroOfertante(datosDeRegistro)

        val id_ofertante = ofertanteService.getIdPorEmail("mock@gmail.com")

        Assertions.assertEquals(1, id_ofertante)
    }

    @Test
    fun excepcionPorObtenerIdPorEmailNoRegistrado() {
        Assertions.assertThrows(OfertanteNotFoundException::class.java) {
            ofertanteService.getIdPorEmail("mock@gmail.com")
        }
    }

    @Test
    fun crearOferta() {
        val dto = crearOfertanteConOferta()

        Assertions.assertNotNull(dto.id)
        Assertions.assertEquals("Desarrollador Full Stack", dto.titulo)
        Assertions.assertEquals("Hyper-MegaRed", dto.empresa)
        Assertions.assertEquals(Modalidad.Remoto, dto.modalidad)
        Assertions.assertEquals(1000, dto.sueldoMin)
        Assertions.assertEquals(2000, dto.sueldoMax)
        Assertions.assertEquals("CABA", dto.ubicacion)
        Assertions.assertEquals(0, dto.cvsRecibidos.size)
    }

    @Test
    fun crearOfertaApareceEnOfertasCreadas() {
        val dto = crearOfertanteConOferta()

        val ofertante = ofertanteService.recuperarOfertante(1)
        Assertions.assertEquals(1, ofertante.ofertasCreadas.size)
        Assertions.assertEquals(dto.titulo, ofertante.ofertasCreadas[0].titulo)
    }

    @Test
    fun crearOfertaOfertanteInexistente() {
        assertThrows<OfertanteNotFoundException> {
            ofertanteService.crearOferta(
                999, OfertaCreateRequest(
                    titulo = "Título",
                    descripcion = "Descripción",
                    modalidad = Modalidad.Presencial,
                    sueldoMin = 500,
                    sueldoMax = 1000,
                    ubicacion = "La Plata"
                )
            )
        }
    }

    @Test
    fun toggleEstadoOfertaCierraOfertaAbierta() {
        val oferta = crearOfertanteConOferta()

        val resultado = ofertanteService.toggleEstadoOferta(1, oferta.id)

        Assertions.assertEquals(EstadoOferta.Cerrado, resultado.estado)
    }

    @Test
    fun toggleEstadoOfertaAbreOfertaCerrada() {
        val oferta = crearOfertanteConOferta()
        ofertanteService.toggleEstadoOferta(1, oferta.id)

        val resultado = ofertanteService.toggleEstadoOferta(1, oferta.id)

        Assertions.assertEquals(EstadoOferta.Abierto, resultado.estado)
    }

    @Test
    fun toggleEstadoOfertaOfertanteInexistente() {
        val oferta = crearOfertanteConOferta()

        assertThrows<OfertanteNotFoundException> {
            ofertanteService.toggleEstadoOferta(999, oferta.id)
        }
    }

    @Test
    fun toggleEstadoOfertaOfertaInexistente() {
        ofertanteService.registroOfertante(OfertanteRegistryDTO("Mock", "Hyper-MegaRed", "marco@gmail.com", "pass"))

        assertThrows<OfferNotFoundException> {
            ofertanteService.toggleEstadoOferta(1, 999)
        }
    }

    @Test
    fun toggleEstadoOfertaOfertanteNoEsDuenio() {
        val oferta = crearOfertanteConOferta()
        ofertanteService.registroOfertante(OfertanteRegistryDTO("Otro", "OtraEmpresa", "otro@gmail.com", "pass"))

        assertThrows<AccessDeniedToFileException> {
            ofertanteService.toggleEstadoOferta(2, oferta.id)
        }
    }
}
