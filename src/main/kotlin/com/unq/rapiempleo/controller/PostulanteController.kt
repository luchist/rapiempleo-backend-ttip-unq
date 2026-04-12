package com.unq.rapiempleo.controller

import com.unq.rapiempleo.dto.OfertaCardDTO
import com.unq.rapiempleo.service.OfertaService
import com.unq.rapiempleo.service.PostulanteService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Transactional
@RestController("/postulante")
class PostulanteController {

    @Autowired
    private lateinit var ofertaService: OfertaService
    @Autowired
    private lateinit var postulanteService: PostulanteService

    @GetMapping
    fun postularseA (idOferta : Long) :ResponseEntity<List<OfertaCardDTO>>{

        return ResponseEntity(HttpStatus.OK)
    }
}