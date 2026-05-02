package com.unq.rapiempleo

import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.OfertanteRepository
import com.unq.rapiempleo.service.OfertaService
import com.unq.rapiempleo.service.OfertanteService
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

@SpringBootTest
class OfertanteServiceTests {

    @Autowired
    private lateinit var ofertanteService: OfertanteService
    @Autowired
    private lateinit var ofertaService: OfertaService
    @Autowired
    private lateinit var ofertanteRepository: OfertanteRepository
    @Autowired
    private lateinit var ofertaRepository: OfertaRepository

    @Transactional
    @Test
    fun crearOfertante() {
        val datosDeRegistro = OfertanteRegistryDTO("Marcos", "Compu-Hyper-MegaRed", "marco@gmail.com", "pass")
        ofertanteService.registroOfertante(datosDeRegistro)

        val ofertanteRegistrado = ofertanteService.recuperarOfertante(2)

        Assertions.assertEquals(datosDeRegistro.nombre, ofertanteRegistrado.nombre)
        Assertions.assertEquals(datosDeRegistro.empresa, ofertanteRegistrado.empresa)
        Assertions.assertEquals(0, ofertanteRegistrado.avisoNuevaOferta.size)
        Assertions.assertEquals(0, ofertanteRegistrado.ofertasCreadas.size)
    }
}