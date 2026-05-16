package com.unq.rapiempleo


import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.model.Oferta
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.service.SearchService
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SearchServiceTests {

    @Autowired
    private lateinit var searchService: SearchService
    @Autowired
    private lateinit var ofertaRepository: OfertaRepository

    @BeforeAll
    fun setOffers(): Unit {
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
        ofertaRepository.saveAll(listOf(oferta1, oferta2, oferta3))
    }


    //@Transactional
    @Test
    fun busquedaPorNombre() {
        val resultadoBusqueda = this.searchService.searchByTitle("ayudante de cocina")
        val verificacionBusqueda = resultadoBusqueda.filter { oferta -> oferta.titulo.contains("ayudante de cocina", true) }

        Assertions.assertEquals(resultadoBusqueda.size, 1)
        Assertions.assertEquals(resultadoBusqueda.size, verificacionBusqueda.size)

        Assertions.assertEquals("La Farola", verificacionBusqueda.first().empresa)
        Assertions.assertTrue(verificacionBusqueda.first().ubicacion.contains("Lujan"))
        Assertions.assertEquals(Modalidad.Presencial, verificacionBusqueda.first().modalidad)
    }

    //@Transactional
    @Test
    fun busquedaConMultiplesResultados() {
        val resultadoBusqueda = this.searchService.searchByTitle("traductor")
        val verificacionBusqueda = resultadoBusqueda.filter { oferta -> oferta.titulo.contains("traductor", true) }

        Assertions.assertEquals(resultadoBusqueda.size, 2)
        Assertions.assertEquals(resultadoBusqueda.size, verificacionBusqueda.size)
    }

    @Test
    fun busquedaAvanzadaSoloPorTitulo() {
        val resultadoBusqAvanzada = this.searchService.buscarConFiltros("ayudante", "", "", "")
        Assertions.assertEquals(1, resultadoBusqAvanzada.size)
    }

    @Test
    fun busquedaAvanzadaPorEmpresa() {
        val resultadoBusqAvanzada = this.searchService.buscarConFiltros("", "Farola", "", "")
        Assertions.assertEquals(1, resultadoBusqAvanzada.size)
        Assertions.assertEquals("La Farola", resultadoBusqAvanzada.first().empresa)
    }

    @Test
    fun busquedaAvanzadaPorModalidad() {
        val resultadoBusqAvanzada = this.searchService.buscarConFiltros("", "", "Hibrido", "")
        val verificacionBusqueda = resultadoBusqAvanzada.filter { oferta -> oferta.modalidad == Modalidad.Hibrido }
        val ofertaAComprobar = resultadoBusqAvanzada.filter { oferta -> oferta.titulo.contains("traductor", true) }

        Assertions.assertEquals(4, resultadoBusqAvanzada.size)
        Assertions.assertEquals(4, verificacionBusqueda.size)
        Assertions.assertEquals(1, ofertaAComprobar.size)
        Assertions.assertEquals("Traductor en Eventos", ofertaAComprobar.first().titulo)
    }

    @Test
    fun busquedaAvanzadaPorUbicacion() {
        val resultadoBusqAvanzada = this.searchService.buscarConFiltros("", "", "", "Buenos Aires")
        Assertions.assertEquals(10, resultadoBusqAvanzada.size)
        Assertions.assertTrue(resultadoBusqAvanzada.filter{ oferta -> oferta.titulo == "Ayudante de cocina"}.isNotEmpty())
        Assertions.assertTrue(resultadoBusqAvanzada.filter{ oferta -> oferta.titulo == "Traductor de documentos"}.isNotEmpty())
        Assertions.assertTrue(resultadoBusqAvanzada.filter{ oferta -> oferta.titulo == "Traductor en Eventos"}.isNotEmpty())
    }

    @Test
    fun busquedaAvanzadaPorMultiplesCampos() {
        val resultadoBusqAvanzada = this.searchService.buscarConFiltros("cocina", "La Farola", "Presencial", "")
        Assertions.assertEquals(1, resultadoBusqAvanzada.size)
        Assertions.assertEquals("Ayudante de cocina", resultadoBusqAvanzada.first().titulo)
        Assertions.assertEquals(32000, resultadoBusqAvanzada.first().sueldoMin)
        Assertions.assertEquals(42000, resultadoBusqAvanzada.first().sueldoMax)
        Assertions.assertEquals("Lujan, Buenos Aires", resultadoBusqAvanzada.first().ubicacion)
    }

    @Test
    fun busquedaAvanzadaPorMultiplesCamposSinResultados() {
        val resultadoBusqAvanzada = this.searchService.buscarConFiltros("Ayudante de cocina", "La Farola",
                                                                        "Remoto", "Lujan, Buenos Aires")
        Assertions.assertEquals(0, resultadoBusqAvanzada.size)
    }

}

