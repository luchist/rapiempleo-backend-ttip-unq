package com.unq.rapiempleo.controller

import com.unq.rapiempleo.dto.OfertaDTO
import com.unq.rapiempleo.service.OfertaService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Transactional
@RequestMapping("/oferta")
@RestController
class OfertaController {

    @Autowired
    private lateinit var ofertaService: OfertaService

    @GetMapping("/obtenerOfertas")
    fun obtenerOfertas() : ResponseEntity<List<OfertaDTO>> {
        val ofertas = ofertaService.recuperarTodasLasOfertas()
        return ResponseEntity(ofertas, HttpStatus.OK)
    }

    @GetMapping("/{nombreOferta}")
    fun buscarOfertas(@PathVariable nombreOferta : String) : ResponseEntity<List<OfertaDTO>> {
        val ofertas = ofertaService.buscarOfertas(nombreOferta)
        return ResponseEntity(ofertas, HttpStatus.OK)
    }
}