package com.unq.rapiempleo.controller

import com.unq.rapiempleo.exceptions.AccessDeniedToFileException
import com.unq.rapiempleo.exceptions.FileNotFoundException
import com.unq.rapiempleo.repository.OfertanteRepository
import com.unq.rapiempleo.repository.PostulanteRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Paths

@Transactional
@RestController
@RequestMapping("/files")
class CvController(
    private val postulanteRepository: PostulanteRepository,
    private val ofertanteRepository: OfertanteRepository,
    @Value("\${app.upload.dir}") private val uploadDir: String
) {

    @GetMapping("/cvs/{idPostulante}/{filename}")
    fun servirCv(
        @PathVariable idPostulante: Long,
        @PathVariable filename: String
    ): ResponseEntity<Resource> {

        // Validations
        val emailDelToken = SecurityContextHolder.getContext().authentication?.principal as? String
            ?: throw AccessDeniedToFileException()

        // Ruptura si ofertante revisa cv de otro
        val postulante = postulanteRepository.findByEmail(emailDelToken)
        if(postulante != null) {
            if (postulante.id_postulante != idPostulante) {
                throw AccessDeniedToFileException()
            }
        } else {
            val ofertante = ofertanteRepository.findByEmail(emailDelToken) ?: throw AccessDeniedToFileException()
        }

        //val postulante = postulanteRepository.findByEmail(emailDelToken)
        //    ?: throw PostulanteNotFoundException()

        //if (postulante.id_postulante != idPostulante) {
        //    throw AccessDeniedToFileException()
        //}

        // Path traversal
        val dirPostulante = Paths.get(uploadDir, idPostulante.toString()).normalize()
        val archivoPath = dirPostulante.resolve(filename).normalize()

        if (!archivoPath.startsWith(dirPostulante)) {
            throw AccessDeniedToFileException()
        }

        val recurso = FileSystemResource(archivoPath)
        if (!recurso.exists()) {
            throw FileNotFoundException()
        }

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"${filename}\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(recurso)
    }
}
