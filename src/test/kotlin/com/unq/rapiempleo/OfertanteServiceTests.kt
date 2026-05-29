package com.unq.rapiempleo

import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import com.unq.rapiempleo.dto.UsuarioLoginDTO
import com.unq.rapiempleo.repository.OfertanteRepository
import com.unq.rapiempleo.service.LoginService
import com.unq.rapiempleo.service.OfertanteService
import jakarta.transaction.Transactional
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class OfertanteServiceTests {

    @Autowired
    private lateinit var ofertanteService: OfertanteService
    @Autowired
    private lateinit var ofertanteRepository: OfertanteRepository
    @Autowired
    private lateinit var loginService : LoginService

    @AfterEach
    fun cleanUp() {
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
}