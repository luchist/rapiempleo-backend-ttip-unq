package com.unq.rapiempleo

import com.unq.rapiempleo.service.OfertaService
import com.unq.rapiempleo.service.PostulanteService
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PostulanteServiceTests {

    @Autowired
    private lateinit var postulanteService : PostulanteService
    @Autowired
    private lateinit var ofertaService: OfertaService


    @Transactional
    @Test
    fun postularAOferta() {
        var postulante = this.postulanteService.getPostulante(1)
        this.postulanteService.postularEnOferta(3, postulante.cv)

        val ofertaPostulada = this.ofertaService.recuperarOferta(3)
        postulante = this.postulanteService.getPostulante(1)

        Assertions.assertEquals(1, ofertaPostulada.postulantes.size)
        Assertions.assertEquals(postulante.cv.dni, ofertaPostulada.postulantes[0].cv.dni)
        Assertions.assertEquals(1, postulante.postulaciones.size)
    }
}