package com.unq.rapiempleo.controller

import com.unq.rapiempleo.dto.PostulanteDTO
import com.unq.rapiempleo.model.Postulante
import com.unq.rapiempleo.repository.PostulanteRepository
import com.unq.rapiempleo.service.OfertaService
import com.unq.rapiempleo.service.PostulanteService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Transactional
@RequestMapping("/postulante")
@RestController
class PostulanteController {

    @Autowired
    private lateinit var ofertaService: OfertaService
    @Autowired
    private lateinit var postulanteService: PostulanteService

    @PostMapping("/{idPostulante}/{idOferta}")
    fun postularseA (@PathVariable idOferta : Long, @PathVariable idPostulante : Long) :ResponseEntity<String>{
        postulanteService.postularEnOferta(idOferta, idPostulante)
        return ResponseEntity("La postulación fue exitosa",HttpStatus.OK)
    }

    @GetMapping("/{idPostulante}")
    fun postulantePorId (@PathVariable idPostulante: Long) : ResponseEntity<PostulanteDTO> {
        var postulante = postulanteService.getPostulante(idPostulante);
        return ResponseEntity(postulante, HttpStatus.OK)
    }

}