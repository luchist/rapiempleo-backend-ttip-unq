package com.unq.rapiempleo

import com.unq.rapiempleo.service.OfertaService
import com.unq.rapiempleo.service.PostulanteService
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class OfertaServiceTests {

    @Autowired
    private lateinit var postulanteService : PostulanteService
    @Autowired
    private lateinit var ofertaService: OfertaService



    @Transactional
    @Test
    fun obtenerTodasLasOfertas() {
        var todasLasOfertas = this.ofertaService.recuperarTodasLasOfertas()
        Assertions.assertEquals(11, todasLasOfertas.size)
    }

    @Transactional
    @Test
    fun busquedaDeOfertas() {
        val resultadoBusqueda = this.ofertaService.buscarOfertas("desarrollador")
        val verificacionBusqueda = resultadoBusqueda.filter { oferta -> oferta.titulo.contains("desarrollador", true) }
        Assertions.assertEquals(resultadoBusqueda.size, 2)
        Assertions.assertEquals(resultadoBusqueda.size, verificacionBusqueda.size)
    }
}