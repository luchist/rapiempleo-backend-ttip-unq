package com.unq.rapiempleo

import com.unq.rapiempleo.dto.PostulanteRegistryDTO
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.OfertanteRepository
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
    @Autowired
    private lateinit var ofertanteRepository: OfertanteRepository
    @Autowired
    private lateinit var ofertaRepository: OfertaRepository


    @Transactional
    @Test
    fun postularAOferta() {
        //var postulante = this.postulanteService.getPostulante(1)
        this.postulanteService.postularEnOferta(3, 1)

        val ofertaPostulada = this.ofertaRepository.findById(3).get()
        var postulante = this.postulanteService.getPostulante(1)

        Assertions.assertEquals(1, ofertaPostulada.postulantes.size)
        Assertions.assertEquals(postulante.cv.dni, ofertaPostulada.postulantes[0].cv.dni)
        Assertions.assertEquals(1, postulante.postulaciones.size)
    }

    @Transactional
    @Test
    fun recibirPostulacionEnOferta() {
        //val postulante = this.postulanteService.getPostulante(1)
        this.postulanteService.postularEnOferta(2, 1)

        val ofertante = this.ofertanteRepository.findById(1).get()
        val ofertaPostulada = this.ofertaService.recuperarOferta(2)

        Assertions.assertTrue(ofertante.nuevaNotifcacion)
        Assertions.assertEquals(1, ofertante.avisosNuevaOferta.size)
        Assertions.assertEquals(ofertaPostulada.titulo, ofertante.avisosNuevaOferta.first())
    }

    @Transactional
    @Test
    fun crearPostulante() {
        val datosDeRegistro = PostulanteRegistryDTO("Chris CampoRojo", "christan52@gmail.com", "pass")
        this.postulanteService.registrarUserPostulante(datosDeRegistro)

        val postulanteRegistrado = postulanteService.getPostulante(2)

        Assertions.assertEquals(datosDeRegistro.nombre, postulanteRegistrado.nombrPostulante)
        Assertions.assertEquals(datosDeRegistro.email, postulanteRegistrado.email)
        Assertions.assertEquals(0, postulanteRegistrado.favoritos.size)

    }

}