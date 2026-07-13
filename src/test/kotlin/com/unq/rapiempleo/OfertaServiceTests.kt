package com.unq.rapiempleo

import com.unq.rapiempleo.dto.DeleteCVRequestDTO
import com.unq.rapiempleo.dto.PostulanteRegistryDTO
import com.unq.rapiempleo.exceptions.OfferNotFoundException
import com.unq.rapiempleo.exceptions.SavedCVNotFoundException
import com.unq.rapiempleo.model.EstadoOferta
import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.model.Oferta
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.PostulanteRepository
import com.unq.rapiempleo.service.OfertaService
import com.unq.rapiempleo.service.PostulanteService
import jakarta.transaction.Transactional
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class OfertaServiceTests {

    @Autowired
    private lateinit var ofertaService: OfertaService
    @Autowired
    private lateinit var ofertaRepository: OfertaRepository
    @Autowired
    private lateinit var postulanteService: PostulanteService
    @Autowired
    private lateinit var postulanteRepository: PostulanteRepository

    @BeforeEach
    fun setUp() {
        val oferta1 = Oferta(
            "Ayudante de cocina", "La Farola", "Vacio", Modalidad.Presencial, EstadoOferta.Abierto,
            32000, 42000, "Lujan, Buenos Aires", true
        )
        val oferta2 = Oferta(
            "Traductor de documentos", "CentiLab", "Vacio", Modalidad.Remoto, EstadoOferta.Abierto,
            24000, 29000, "La Plata, Buenos Aires", false
        )
        val oferta3 = Oferta(
            "Traductor en Eventos", "Embajada de Portugal", "Vacio", Modalidad.Hibrido, EstadoOferta.Abierto,
            33000, 38000, "Retiro, Buenos Aires", true
        )
        val oferta4 = Oferta(
            "Desarrollador Sr", "Tech.Inc", "Vacio", Modalidad.Hibrido, EstadoOferta.Abierto,
            45000, 55000, "Lomas de Zamora, Buenos Aires", favorito = true
        )
        ofertaRepository.saveAll(listOf(oferta1, oferta2, oferta3, oferta4))

        val datosPostulante = PostulanteRegistryDTO("Mock Sanchez", "mock@gmail.com", "passpass")
        postulanteService.registrarUserPostulante(datosPostulante)
    }

    @AfterEach
    fun cleanUp() {
        postulanteRepository.deleteAll()
        postulanteRepository.resetIdPostulante()
        ofertaRepository.deleteAll()
        ofertaRepository.resetIdOferta()
    }

    @Test
    fun obtenerUnaOferta() {
        val ofertaRecuperada = this.ofertaService.recuperarOferta(1, null)
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

    @Test
    fun buscarOfertasPorNombre() {
        val ofertasObtenidas = ofertaService.buscarOfertas("Traductor")
        Assertions.assertEquals(2, ofertasObtenidas.size)
        Assertions.assertTrue( ofertasObtenidas.any { oferta -> oferta.titulo == "Traductor de documentos" })
        Assertions.assertTrue( ofertasObtenidas.any { oferta -> oferta.titulo == "Traductor en Eventos" })
    }

    @Test
    fun excepcionRecuperarOfertaInexistente() {
        assertThrows<OfferNotFoundException> {
            ofertaService.recuperarOferta(999, null)
        }
    }

    @Test
    fun excepcionEliminarCvPostulacionDeOfertaInexistente() {
        val request = DeleteCVRequestDTO(1, 999)
        assertThrows<OfferNotFoundException> {
            ofertaService.eliminarCVPostulacion(request)
        }
    }

    @Transactional
    @Test
    fun excepcionEliminarCvPostulacionInexistenteEnLaOferta() {
        val idOferta = ofertaService.recuperarTodasLasOfertas().first().id
        val request = DeleteCVRequestDTO(99, idOferta)
        assertThrows<SavedCVNotFoundException> {
            ofertaService.eliminarCVPostulacion(request)
        }
    }

    @Test
    fun recuperarTodasLasOfertasConFavoritosMarcaElFavorito() {
        val idPostulante = postulanteService.getIdPorEmail("mock@gmail.com")
        val idOfertaFavorita = ofertaService.recuperarTodasLasOfertas().first { !it.favorito }.id
        postulanteService.agregarOfertaFavorita(idPostulante, idOfertaFavorita)

        val ofertas = ofertaService.recuperarTodasLasOfertasYFavoritos(idPostulante)

        Assertions.assertTrue(ofertas.first { it.id == idOfertaFavorita }.favorito)
        postulanteService.removerOfertaFavorita(idPostulante, idOfertaFavorita)
    }

}