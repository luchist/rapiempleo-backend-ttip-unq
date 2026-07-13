package com.unq.rapiempleo.controller

import com.unq.rapiempleo.dto.OfertaCardDTO
import com.unq.rapiempleo.security.UsuarioAutenticado
import com.unq.rapiempleo.service.SearchService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Transactional
@RequestMapping("/search")
@RestController
class SearchController {

    @Autowired
    private lateinit var searchService: SearchService

    @GetMapping
    fun buscarOfertas(
        @RequestParam(required = false) q: String?,
        @AuthenticationPrincipal usuario: UsuarioAutenticado?
    ): ResponseEntity<List<OfertaCardDTO>> {
        val idPostulante = if (usuario?.esPostulante == true) usuario.id else null

        val ofertas = searchService.busquedaInteligente(q, idPostulante)
        return ResponseEntity(ofertas, HttpStatus.OK)
    }
}
