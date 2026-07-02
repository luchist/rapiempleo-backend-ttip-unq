package com.unq.rapiempleo.controller

import com.unq.rapiempleo.dto.OfertaCardDTO
import com.unq.rapiempleo.exceptions.AccessDeniedToFileException
import com.unq.rapiempleo.service.SearchService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
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
        @RequestParam(required = false) q: String?
    ): ResponseEntity<List<OfertaCardDTO>> {
        val auth = SecurityContextHolder.getContext().authentication
            ?: throw AccessDeniedToFileException()
        val userId = auth.details as Long
        val isPostulante = auth.authorities.any { it.authority == "ROLE_POSTULANTE" }

        val ofertas = searchService.busquedaInteligente(q, if (isPostulante) userId else null)
        return ResponseEntity(ofertas, HttpStatus.OK)
    }
}
