package com.unq.rapiempleo.controller

import com.unq.rapiempleo.dto.AvisoPostulanteDTO
import com.unq.rapiempleo.dto.CvCollectRequestDTO
import com.unq.rapiempleo.dto.DeleteCVRequestDTO
import com.unq.rapiempleo.dto.OfertaCreadaDTO
import com.unq.rapiempleo.dto.OfertaCreateRequest
import com.unq.rapiempleo.dto.OfertanteDTO
import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import com.unq.rapiempleo.exceptions.AccessDeniedToFileException
import com.unq.rapiempleo.exceptions.UnauthenticatedException
import com.unq.rapiempleo.model.CvSummary
import com.unq.rapiempleo.service.ImageStorageService
import com.unq.rapiempleo.service.OfertaService
import com.unq.rapiempleo.service.OfertanteService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Transactional
@RequestMapping("/ofertante")
@RestController
class OfertanteController {

    @Autowired
    private lateinit var ofertanteService: OfertanteService
    @Autowired
    private lateinit var imageStorageService: ImageStorageService
    @Autowired
    private lateinit var ofertaService: OfertaService

    @GetMapping("/{idOfertante}")
    fun obtenerOfertante(@PathVariable idOfertante : Long) : ResponseEntity<OfertanteDTO> {
        val ofertante = ofertanteService.recuperarOfertante(idOfertante)
        return ResponseEntity(ofertante, HttpStatus.OK)
    }

    @PostMapping("/registrar")
    fun registroOfertante(@RequestBody registroOfertante : OfertanteRegistryDTO) : ResponseEntity<Map<String, String>> {
        ofertanteService.registroOfertante(registroOfertante)
        return ResponseEntity(mapOf("message" to "Su registro fue exitoso"), HttpStatus.OK)
    }

    @PostMapping("/{idOfertante}/foto")
    fun subirImagenPerfil(
        @PathVariable idOfertante: Long,
        @RequestParam("file") archivo: MultipartFile
    ): ResponseEntity<Map<String, String>> {
        verificarOfertante(idOfertante)
        val fotoPath = imageStorageService.guardarImagenPerfilOfertante(idOfertante, archivo)
        ofertanteService.actualizarImagenPerfil(idOfertante, fotoPath)
        return ResponseEntity(mapOf("fotoPath" to fotoPath), HttpStatus.OK)
    }

    @PostMapping("/{idOfertante}/oferta")
    fun crearOferta(
        @PathVariable idOfertante: Long,
        @RequestBody request: OfertaCreateRequest
    ): ResponseEntity<OfertaCreadaDTO> {
        verificarOfertante(idOfertante)
        val oferta = ofertanteService.crearOferta(idOfertante, request)
        return ResponseEntity(oferta, HttpStatus.OK)
    }

    @PatchMapping("/{idOfertante}/oferta/{idOferta}/estado")
    fun toggleEstadoOferta(
        @PathVariable idOfertante: Long,
        @PathVariable idOferta: Long
    ): ResponseEntity<OfertaCreadaDTO> {
        verificarOfertante(idOfertante)
        val oferta = ofertanteService.toggleEstadoOferta(idOfertante, idOferta)
        return ResponseEntity(oferta, HttpStatus.OK)
    }

    private fun verificarOfertante(idOfertante: Long) {
        val auth = SecurityContextHolder.getContext().authentication
            ?: throw UnauthenticatedException()
        if (auth.details as Long != idOfertante)
            throw AccessDeniedToFileException()
    }

    @DeleteMapping("/deleteNotify/{idOfertante}/{idNotify}")
    fun deleteNotificaction(@PathVariable idOfertante: Long, @PathVariable idNotify: Long) : ResponseEntity<String> {
        ofertanteService.eliminarNotificacion(idOfertante, idNotify)
        return ResponseEntity("Notificación eliminada exitosa", HttpStatus.OK)
    }

    @PostMapping("/saveCV")
    fun saveCurriculum(@RequestBody cvAGuardar : CvCollectRequestDTO) : ResponseEntity<String> {
        ofertanteService.guardarCV(cvAGuardar)
        return ResponseEntity("Se guardo el CV exitosamente", HttpStatus.OK)
    }

    @DeleteMapping("/deleteSavedCV")
    fun deleteCurriculum(@RequestBody cvAEliminar : CvCollectRequestDTO) : ResponseEntity<String> {
        ofertanteService.eliminarCVGuardado(cvAEliminar)
        return ResponseEntity("Se elimino el CV exitosamente", HttpStatus.OK)
    }

    @DeleteMapping("/deletePostulationCV")
    fun deletePostulationCV(@RequestBody cvAEliminar: DeleteCVRequestDTO) : ResponseEntity<String> {
        ofertaService.eliminarCVPostulacion(cvAEliminar)
        return ResponseEntity("Se elimino el CV exitosamente", HttpStatus.OK)
    }
}
