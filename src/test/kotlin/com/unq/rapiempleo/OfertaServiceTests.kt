package com.unq.rapiempleo

import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.model.Oferta
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.service.OfertaService
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

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest
class OfertaServiceTests {

    @Autowired
    private lateinit var ofertaService: OfertaService
    @Autowired
    private lateinit var ofertaRepository: OfertaRepository

    @BeforeEach
    fun setUp() {
        val oferta1 = Oferta(
            "Ayudante de cocina", "La Farola", "Vacio", Modalidad.Presencial, "Abierto",
            32000, 42000, "Lujan, Buenos Aires", true
        )
        val oferta2 = Oferta(
            "Traductor de documentos", "CentiLab", "Vacio", Modalidad.Remoto, "Abierto",
            24000, 29000, "La Plata, Buenos Aires", false
        )
        val oferta3 = Oferta(
            "Traductor en Eventos", "Embajada de Portugal", "Vacio", Modalidad.Hibrido, "Abierto",
            33000, 38000, "Retiro, Buenos Aires", true
        )
        val oferta4 = Oferta(
            "Desarrollador Sr", "Tech.Inc", "Vacio", Modalidad.Hibrido, "Abierto",
            45000, 55000, "Lomas de Zamora, Buenos Aires", favorito = true
        )
        ofertaRepository.saveAll(listOf(oferta1, oferta2, oferta3, oferta4))
    }

    @AfterEach
    fun cleanUp() {
        ofertaRepository.deleteAll()
        ofertaRepository.resetIdOferta()
    }

    @Test
    fun obtenerUnaOferta() {
        val ofertaRecuperada = this.ofertaService.recuperarOferta(1)
        Assertions.assertEquals("Ayudante de cocina", ofertaRecuperada.titulo)
        Assertions.assertEquals("La Farola", ofertaRecuperada.empresa)
        Assertions.assertEquals("Lujan, Buenos Aires", ofertaRecuperada.ubicacion)
    }

    @Transactional
    @Test
    fun obtenerTodasLasOfertas() {
        val todasLasOfertas = this.ofertaService.recuperarTodasLasOfertas()
        Assertions.assertEquals(4, todasLasOfertas.size)
    }

}