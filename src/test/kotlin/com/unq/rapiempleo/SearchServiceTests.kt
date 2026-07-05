package com.unq.rapiempleo


import com.unq.rapiempleo.model.EstadoOferta
import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.model.Oferta
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.service.SearchService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SearchServiceTests {

    @Autowired
    private lateinit var searchService: SearchService
    @Autowired
    private lateinit var ofertaRepository: OfertaRepository

    @BeforeEach
    fun setOffers() {
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
        val ofertaConTecnologia = Oferta(
            "Desarrollador Backend", "CloudSync", "Buscamos experiencia solida en Kubernetes y microservicios",
            Modalidad.Remoto, EstadoOferta.Abierto, 50000, 60000, "Cordoba, Argentina", false
        )
        val ofertaCerrada = Oferta(
            "Contador Sr", "Tepago SA", "Vacio", Modalidad.Presencial, EstadoOferta.Cerrado,
            40000, 44000, "Temperley, Buenos Aires", false
        )
        ofertaRepository.saveAll(listOf(oferta1, oferta2, oferta3, ofertaConTecnologia, ofertaCerrada))
    }

    @AfterEach
    fun cleanOffers() {
        ofertaRepository.deleteAll()
        ofertaRepository.resetIdOferta()
    }

    @Test
    fun busquedaPorTituloEncuentraLaOferta() {
        val resultado = searchService.busquedaInteligente("cocina", null)
        val verificacion = resultado.filter { it.titulo.contains("cocina", true) }

        Assertions.assertEquals(1, resultado.size)
        Assertions.assertEquals(resultado.size, verificacion.size)
        Assertions.assertEquals("La Farola", verificacion.first().empresa)
        Assertions.assertTrue(verificacion.first().ubicacion.contains("Lujan"))
        Assertions.assertEquals(Modalidad.Presencial, verificacion.first().modalidad)
    }

    @Test
    fun busquedaConMultiplesResultados() {
        val resultado = searchService.busquedaInteligente("traductor", null)
        val verificacion = resultado.filter { it.titulo.contains("traductor", true) }

        Assertions.assertEquals(2, resultado.size)
        Assertions.assertEquals(resultado.size, verificacion.size)
    }

    @Test
    fun busquedaPorPalabraParcialUsaElComodin() {
        // "traduc" must still match "Traductor ..." thanks to the boolean-mode prefix wildcard.
        val resultado = searchService.busquedaInteligente("traduc", null)
        Assertions.assertEquals(2, resultado.size)
    }

    @Test
    fun busquedaPorEmpresa() {
        val resultado = searchService.busquedaInteligente("Farola", null)
        Assertions.assertEquals(1, resultado.size)
        Assertions.assertEquals("La Farola", resultado.first().empresa)
    }

    @Test
    fun busquedaPorUbicacion() {
        val resultado = searchService.busquedaInteligente("Buenos Aires", null)
        Assertions.assertEquals(3, resultado.size)
        Assertions.assertTrue(resultado.any { it.titulo == "Ayudante de cocina" })
        Assertions.assertTrue(resultado.any { it.titulo == "Traductor de documentos" })
        Assertions.assertTrue(resultado.any { it.titulo == "Traductor en Eventos" })
    }

    @Test
    fun busquedaCoincideConElCuerpoDeLaDescripcion() {
        val resultado = searchService.busquedaInteligente("Kubernetes", null)
        Assertions.assertEquals(1, resultado.size)
        Assertions.assertEquals("Desarrollador Backend", resultado.first().titulo)
    }

    @Test
    fun busquedaVaciaDevuelveTodasLasOfertasAbiertas() {
        Assertions.assertEquals(4, searchService.busquedaInteligente("", null).size)
        Assertions.assertEquals(4, searchService.busquedaInteligente(null, null).size)
    }

    @Test
    fun ofertaCerradaNoApareceEnLaBusqueda() {
        Assertions.assertEquals(0, searchService.busquedaInteligente("Contador", null).size)
        Assertions.assertEquals(0, searchService.busquedaInteligente("Tepago", null).size)
    }

    @Test
    fun busquedaConIdPostulanteSinFavoritosNoMarcaFavoritos() {
        val resultado = searchService.busquedaInteligente("Kubernetes", 999L)

        Assertions.assertEquals(1, resultado.size)
        Assertions.assertTrue(resultado.none { it.favorito })
    }
}
