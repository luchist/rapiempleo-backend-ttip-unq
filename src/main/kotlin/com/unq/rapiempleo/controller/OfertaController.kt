package com.unq.rapiempleo.controller

import com.unq.rapiempleo.dto.OfertaCardDTO
import com.unq.rapiempleo.dto.OfertaDTO
import com.unq.rapiempleo.exceptions.AccessDeniedToFileException
import com.unq.rapiempleo.security.UsuarioAutenticado
import com.unq.rapiempleo.service.OfertaService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
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

    @GetMapping("/{idOferta}")
    fun obtenerOferta(
        @PathVariable idOferta : Long,
        @AuthenticationPrincipal usuario: UsuarioAutenticado?
    ) : ResponseEntity<OfertaDTO> {
        val idPostulante = if (usuario?.esPostulante == true) usuario.id else null
        val oferta = ofertaService.recuperarOferta(idOferta, idPostulante)
        return ResponseEntity(oferta, HttpStatus.OK)
    }

    @GetMapping("/obtenerOfertas")
    fun obtenerOfertas() : ResponseEntity<List<OfertaCardDTO>> {
        val ofertas = ofertaService.recuperarTodasLasOfertas()
        return ResponseEntity(ofertas, HttpStatus.OK)
    }

    @GetMapping("/recuperarOfertasYFavoritos")
    fun obtenerOfertasConFavoritos(
        @AuthenticationPrincipal usuario: UsuarioAutenticado?
    ) : ResponseEntity<List<OfertaCardDTO>> {
        val userId = usuario?.id ?: throw AccessDeniedToFileException()
        val ofertas = ofertaService.recuperarTodasLasOfertasYFavoritos(userId)
        return ResponseEntity(ofertas, HttpStatus.OK)
    }
}