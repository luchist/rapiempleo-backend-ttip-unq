package com.unq.rapiempleo.controller

import com.unq.rapiempleo.dto.OfertaCardDTO
import com.unq.rapiempleo.service.SearchService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Transactional
@RequestMapping("/search")
@RestController
class SearchController {

    @Autowired
    private lateinit var searchService: SearchService

    @GetMapping("/{title}")
    fun buscarOfertas(@PathVariable title : String) : ResponseEntity<List<OfertaCardDTO>> {
        val ofertas = searchService.searchByTitle(title)
        return ResponseEntity(ofertas, HttpStatus.OK)
    }

    @GetMapping
    fun buscarOfertas(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) company: String?,
        @RequestParam(required = false) workType: String?,
        @RequestParam(required = false) location: String?
    ): ResponseEntity<List<OfertaCardDTO>> {
        val ofertas = searchService.buscarConFiltros(title, company, workType, location)
        return ResponseEntity(ofertas, HttpStatus.OK)
    }
}