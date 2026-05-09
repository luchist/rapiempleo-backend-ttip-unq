package com.unq.rapiempleo.controller

import com.unq.rapiempleo.dto.OfertanteDTO
import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import com.unq.rapiempleo.service.OfertanteService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Transactional
@RequestMapping("/ofertante")
@RestController
class OfertanteController {

    @Autowired
    private lateinit var ofertanteService: OfertanteService


    @GetMapping("/{idOfertante}")
    fun obtenerOfertante(@PathVariable idOfertante : Long) : ResponseEntity<OfertanteDTO> {
        val ofertante = ofertanteService.recuperarOfertante(idOfertante)
        return ResponseEntity(ofertante, HttpStatus.OK)
    }

    @PostMapping("/registrar")
    fun registroOfertante(@RequestBody registroOfertante : OfertanteRegistryDTO) : ResponseEntity<String> {
        ofertanteService.registroOfertante(registroOfertante)
        return ResponseEntity("El registro fue exitoso", HttpStatus.OK)
    }

    @DeleteMapping("/deleteNotify/{idOfertante}/{idNotify}")
    fun deleteNotificaction(@PathVariable idOfertante: Long, @PathVariable idNotify: Long) : ResponseEntity<String> {
        ofertanteService.eliminarNotificacion(idOfertante, idNotify)
        return ResponseEntity("Notificación eliminada exitosa", HttpStatus.OK)
    }
}