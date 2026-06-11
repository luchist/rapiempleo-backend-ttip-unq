package com.unq.rapiempleo

import com.unq.rapiempleo.dto.OfertaCreateRequest
import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import com.unq.rapiempleo.dto.UsuarioLoginDTO
import com.unq.rapiempleo.exceptions.OfertanteNotFoundException
import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.OfertanteRepository
import com.unq.rapiempleo.service.LoginService
import com.unq.rapiempleo.service.OfertanteService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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
    fun loginOfertante() {
        val datosDeRegistro = OfertanteRegistryDTO("Mock", "RedMega", "marco@gmail.com", "pass")
        ofertanteService.registroOfertante(datosDeRegistro)

        val userLogueado = loginService.loginDeUser(UsuarioLoginDTO("marco@gmail.com", "pass"))

        Assertions.assertEquals(1, userLogueado.id)
        Assertions.assertEquals("Mock", userLogueado.nombre)
        Assertions.assertTrue(userLogueado.token.isNotEmpty())
    }

    @Test
    fun crearOferta() {
        val datosDeRegistro = OfertanteRegistryDTO("Mock", "Hyper-MegaRed", "marco@gmail.com", "pass")
        ofertanteService.registroOfertante(datosDeRegistro)

        val request = OfertaCreateRequest(
            titulo = "Desarrollador Full Stack",
            descripcion = "Descripción del puesto",
            modalidad = Modalidad.Remoto,
            sueldoMin = 1000,
            sueldoMax = 2000,
            ubicacion = "CABA"
        )
        val dto = ofertanteService.crearOferta(1, request)

        Assertions.assertNotNull(dto.id)
        Assertions.assertEquals(request.titulo, dto.titulo)
        Assertions.assertEquals(datosDeRegistro.company, dto.empresa)
        Assertions.assertEquals(request.modalidad, dto.modalidad)
        Assertions.assertEquals(request.sueldoMin, dto.sueldoMin)
        Assertions.assertEquals(request.sueldoMax, dto.sueldoMax)
        Assertions.assertEquals(request.ubicacion, dto.ubicacion)
        Assertions.assertEquals(0, dto.cvsRecibidos.size)
    }

    @Test
    fun crearOfertaApareceEnOfertasCreadas() {
        val datosDeRegistro = OfertanteRegistryDTO("Mock", "Hyper-MegaRed", "marco@gmail.com", "pass")
        ofertanteService.registroOfertante(datosDeRegistro)

        val request = OfertaCreateRequest(
            titulo = "Desarrollador Full Stack",
            descripcion = "Descripción del puesto",
            modalidad = Modalidad.Remoto,
            sueldoMin = 1000,
            sueldoMax = 2000,
            ubicacion = "CABA"
        )
        ofertanteService.crearOferta(1, request)

        val ofertante = ofertanteService.recuperarOfertante(1)
        Assertions.assertEquals(1, ofertante.ofertasCreadas.size)
        Assertions.assertEquals(request.titulo, ofertante.ofertasCreadas[0].titulo)
    }

    @Test
    fun crearOfertaOfertanteInexistente() {
        val request = OfertaCreateRequest(
            titulo = "Título",
            descripcion = "Descripción",
            modalidad = Modalidad.Presencial,
            sueldoMin = 500,
            sueldoMax = 1000,
            ubicacion = "La Plata"
        )
        assertThrows<OfertanteNotFoundException> {
            ofertanteService.crearOferta(999, request)
        }
    }
}