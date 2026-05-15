package com.unq.rapiempleo.controller

import com.unq.rapiempleo.dto.AvisoPostulanteDTO
import com.unq.rapiempleo.dto.PostulanteDTO
import com.unq.rapiempleo.dto.PostulanteRegistryDTO
import com.unq.rapiempleo.service.CvStorageService
import com.unq.rapiempleo.service.OfertaService
import com.unq.rapiempleo.service.PostulanteService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Transactional
@RequestMapping("/postulante")
@RestController
class PostulanteController {

    @Autowired
    private lateinit var ofertaService: OfertaService
    @Autowired
    private lateinit var postulanteService: PostulanteService
    @Autowired
    private lateinit var cvStorageService: CvStorageService

    @PostMapping("/{idPostulante}/{idOferta}")
    fun postularseA (@PathVariable idOferta : Long, @PathVariable idPostulante : Long) :ResponseEntity<String>{
        postulanteService.postularEnOferta(idOferta, idPostulante)
        return ResponseEntity("La postulación fue exitosa",HttpStatus.OK)
    }

    @GetMapping("/{idPostulante}")
    fun postulantePorId (@PathVariable idPostulante: Long) : ResponseEntity<PostulanteDTO> {
        val postulante = postulanteService.getPostulante(idPostulante)
        return ResponseEntity(postulante, HttpStatus.OK)
    }

    @PostMapping("/registrar")
    fun registroPostulante(@RequestBody registroPostulante : PostulanteRegistryDTO) : ResponseEntity<String> {
        postulanteService.registrarUserPostulante(registroPostulante)
        return ResponseEntity("El registro fue exitoso", HttpStatus.OK)
    }

    @PatchMapping("/{idPostulante}/cv/favorito")
    fun setearCvFavorito(
        @PathVariable idPostulante: Long,
        @RequestParam cvPath: String
    ): ResponseEntity<String> {
        postulanteService.setearCvFavorito(idPostulante, cvPath)
        return ResponseEntity("CV favorito actualizado", HttpStatus.OK)
    }

    @PostMapping("/{idPostulante}/cv")
    fun subirCv(
        @PathVariable idPostulante: Long,
        @RequestParam("file") archivo: MultipartFile
    ): ResponseEntity<Map<String, String>> {
        val cvPath = cvStorageService.guardarCv(idPostulante, archivo)
        postulanteService.agregarCv(idPostulante, cvPath)
        return ResponseEntity(mapOf("cvPath" to cvPath), HttpStatus.OK)
    }

    @PostMapping("/cvViewed")
    fun marcarCvComoVisto(@RequestBody postulacionNotif : AvisoPostulanteDTO) : ResponseEntity<String> {
        postulanteService.notificarCvVisto(postulacionNotif)
        return ResponseEntity("Postulante notificado exitosamente", HttpStatus.OK)
    }

    @DeleteMapping("/deleteNotify/{idPostulante}/{idNotify}")
    fun deleteNotificaction(@PathVariable idPostulante: Long, @PathVariable idNotify: Long) : ResponseEntity<String> {
        postulanteService.eliminarNotificacion(idPostulante, idNotify)
        return ResponseEntity("Notificación eliminada exitosamente", HttpStatus.OK)
    }
}