package com.unq.rapiempleo.controller

import com.unq.rapiempleo.dto.AvisoPostulanteDTO
import com.unq.rapiempleo.dto.CvEntryRequestDTO
import com.unq.rapiempleo.dto.PostulacionBoardItemDTO
import com.unq.rapiempleo.dto.PostulanteDTO
import com.unq.rapiempleo.dto.PostulanteRegistryDTO
import com.unq.rapiempleo.model.EstadoPostulacion
import com.unq.rapiempleo.service.CvStorageService
import com.unq.rapiempleo.service.ImageStorageService
import com.unq.rapiempleo.service.PostulanteService
import com.unq.rapiempleo.exceptions.PreferenciasFieldRequiredException
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Transactional
@RequestMapping("/postulante")
@RestController
class PostulanteController {

    @Autowired
    private lateinit var postulanteService: PostulanteService
    @Autowired
    private lateinit var cvStorageService: CvStorageService
    @Autowired
    private lateinit var imageStorageService: ImageStorageService

    @PreAuthorize("hasRole('POSTULANTE') and @autorizacion.esUsuarioActual(#idPostulante, authentication)")
    @PostMapping("/{idPostulante}/{idOferta}")
    fun postularseA (@PathVariable idOferta : Long, @PathVariable idPostulante : Long) :ResponseEntity<String>{
        postulanteService.postularEnOferta(idOferta, idPostulante)
        return ResponseEntity("La postulación fue exitosa",HttpStatus.OK)
    }

    @PreAuthorize("hasRole('POSTULANTE') and @autorizacion.esUsuarioActual(#idPostulante, authentication)")
    @GetMapping("/{idPostulante}")
    fun postulantePorId (@PathVariable idPostulante: Long) : ResponseEntity<PostulanteDTO> {
        val postulante = postulanteService.getPostulante(idPostulante)
        return ResponseEntity(postulante, HttpStatus.OK)
    }

    @PostMapping("/registrar")
    fun registroPostulante(@RequestBody registroPostulante : PostulanteRegistryDTO) : ResponseEntity<Map<String,String>> {
        postulanteService.registrarUserPostulante(registroPostulante)
        return ResponseEntity(mapOf("message" to "Su registro fue exitoso"), HttpStatus.OK)
    }

    @PreAuthorize("hasRole('POSTULANTE') and @autorizacion.esUsuarioActual(#idPostulante, authentication)")
    @PatchMapping("/{idPostulante}/cv/favorito")
    fun setearCvFavorito(
        @PathVariable idPostulante: Long,
        @RequestParam cvPath: String
    ): ResponseEntity<String> {
        postulanteService.setearCvFavorito(idPostulante, cvPath)
        return ResponseEntity("CV favorito actualizado", HttpStatus.OK)
    }

    @PreAuthorize("hasRole('POSTULANTE') and @autorizacion.esUsuarioActual(#idPostulante, authentication)")
    @PostMapping("/{idPostulante}/cv")
    fun subirCv(
        @PathVariable idPostulante: Long,
        @RequestParam("file") archivo: MultipartFile
    ): ResponseEntity<Map<String, String>> {
        val cvPath = cvStorageService.guardarCv(idPostulante, archivo)
        postulanteService.agregarCv(idPostulante, cvPath)
        return ResponseEntity(mapOf("cvPath" to cvPath), HttpStatus.OK)
    }

    @PreAuthorize("hasRole('POSTULANTE') and @autorizacion.esUsuarioActual(#idPostulante, authentication)")
    @PostMapping("/{idPostulante}/foto")
    fun subirImagenPerfil(
        @PathVariable idPostulante: Long,
        @RequestParam("file") archivo: MultipartFile
    ): ResponseEntity<Map<String, String>> {
        val imgPath = imageStorageService.guardarImagenPerfilPostulante(idPostulante, archivo)
        postulanteService.actualizarImagenPerfil(idPostulante, imgPath)
        return ResponseEntity(mapOf("imgPath" to imgPath), HttpStatus.OK)
    }

    @PreAuthorize("hasRole('OFERTANTE')" +
                "and @autorizacion.gestionaOferta(#avisoPostulacion.id_oferta, authentication)")
    @PostMapping("/respuestaCV")
    fun notificarAccionOfertante(@RequestBody avisoPostulacion : AvisoPostulanteDTO) : ResponseEntity<String> {
        postulanteService.notificarAccionEnCv(avisoPostulacion)
        return ResponseEntity("Postulante notificado exitosamente", HttpStatus.OK)
    }

    @PreAuthorize("hasRole('POSTULANTE') and @autorizacion.esUsuarioActual(#idPostulante, authentication)")
    @GetMapping("/{idPostulante}/board")
    fun getBoard(@PathVariable idPostulante: Long) : ResponseEntity<List<PostulacionBoardItemDTO>> {
        val statusBoard = postulanteService.getBoard(idPostulante)
        return ResponseEntity(statusBoard, HttpStatus.OK)
    }

    @PreAuthorize("hasRole('POSTULANTE') and @autorizacion.esUsuarioActual(#idPostulante, authentication)")
    @PatchMapping("/{idPostulante}/board/{idPostulacionEstado}")
    fun updateEstadoPostulacion(
        @PathVariable idPostulante: Long,
        @PathVariable idPostulacionEstado: Long,
        @RequestParam nuevoEstado: EstadoPostulacion
    ): ResponseEntity<String> {
        postulanteService.updateEstadoPostulacion(idPostulante, idPostulacionEstado, nuevoEstado)
        return ResponseEntity("Estado de postulación actualizado exitosamente", HttpStatus.OK)
    }

    @PreAuthorize("hasRole('POSTULANTE') and @autorizacion.esUsuarioActual(#idPostulante, authentication)")
    @DeleteMapping("/deleteNotify/{idPostulante}/{idNotify}")
    fun deleteNotification(@PathVariable idPostulante: Long, @PathVariable idNotify: Long) : ResponseEntity<String> {
        postulanteService.eliminarNotificacion(idPostulante, idNotify)
        return ResponseEntity("Notificación eliminada exitosamente", HttpStatus.OK)
    }

    @PreAuthorize("hasRole('POSTULANTE') and @autorizacion.esUsuarioActual(#idPostulante, authentication)")
    @PostMapping("/addFavorito/{idPostulante}/{idOferta}")
    fun agregarOfertaFavorita(@PathVariable idPostulante: Long, @PathVariable idOferta: Long) : ResponseEntity<String> {
        postulanteService.agregarOfertaFavorita(idPostulante, idOferta)
        return ResponseEntity("Oferta favorita agregada exitosamente", HttpStatus.OK)
    }

    @PreAuthorize("hasRole('POSTULANTE') and @autorizacion.esUsuarioActual(#idPostulante, authentication)")
    @PostMapping("/removeFavorito/{idPostulante}/{idOferta}")
    fun removerOfertaFavorita(@PathVariable idPostulante: Long, @PathVariable idOferta: Long) : ResponseEntity<String> {
        postulanteService.removerOfertaFavorita(idPostulante, idOferta)
        return ResponseEntity("Oferta favorita removida exitosamente", HttpStatus.OK)
    }

    @PreAuthorize("hasRole('POSTULANTE') and @autorizacion.esUsuarioActual(#idPostulante, authentication)")
    @PatchMapping("/{idPostulante}/preferencia")
    fun actualizarPreferencias(
        @PathVariable idPostulante: Long,
        @RequestBody body: Map<String, String>
    ): ResponseEntity<String> {
        val preferencias = body["preferencias"]
            ?: throw PreferenciasFieldRequiredException()

        postulanteService.actualizarPreferencias(idPostulante, preferencias)
        return ResponseEntity("Preferencias actualizadas", HttpStatus.OK)
    }

    @PreAuthorize("hasRole('POSTULANTE')" +
                "and @autorizacion.esUsuarioActual(#cvEntryRequestDTO.idPostulante, authentication)")
    @DeleteMapping("/removeCV")
    fun removerCvDePostulante(@RequestBody cvEntryRequestDTO : CvEntryRequestDTO) : ResponseEntity<String> {
        postulanteService.removerCvIndicado(cvEntryRequestDTO)
        return ResponseEntity("Se elimino el CV indicado", HttpStatus.OK)
    }
}
