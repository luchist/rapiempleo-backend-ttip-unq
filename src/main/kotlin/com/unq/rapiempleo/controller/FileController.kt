package com.unq.rapiempleo.controller

import com.unq.rapiempleo.exceptions.AccessDeniedToFileException
import com.unq.rapiempleo.exceptions.FileNotFoundException
import com.unq.rapiempleo.repository.OfertaRepository
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
class FileController(
    private val ofertaRepository: OfertaRepository,
    @Value("\${app.upload.dir}") private val uploadDir: String,
    @Value("\${app.upload.fotos.dir}") private val fotosDir: String
) {

    @GetMapping("/cvs/{idPostulante}/{filename}")
    fun servirCv(
        @PathVariable idPostulante: Long,
        @PathVariable filename: String
    ): ResponseEntity<Resource> {

        val auth = SecurityContextHolder.getContext().authentication
            ?: throw AccessDeniedToFileException()

        val userId = auth.details as Long
        val isPostulante = auth.authorities.any { it.authority == "ROLE_POSTULANTE" }

        if (isPostulante) {
            if (userId != idPostulante)
                throw AccessDeniedToFileException()
        } else {
            if (!ofertaRepository.existePostulanteEnOfertasDeOfertante(userId, idPostulante))
                throw AccessDeniedToFileException()
        }

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

    @GetMapping("/fotos/{tipo}/{idUsuario}/{filename}")
    fun servirImagenPerfil(
        @PathVariable tipo: String,
        @PathVariable idUsuario: Long,
        @PathVariable filename: String
    ): ResponseEntity<Resource> {

        // Validations
        SecurityContextHolder.getContext().authentication?.principal as? String
            ?: throw AccessDeniedToFileException()

        // Path traversal
        val TIPOS_VALIDOS = setOf("postulante", "ofertante")
        if (tipo !in TIPOS_VALIDOS)
            throw AccessDeniedToFileException()

        val dirUsuario = Paths.get(fotosDir, tipo, idUsuario.toString()).normalize()
        val archivoPath = dirUsuario.resolve(filename).normalize()

        if (!archivoPath.startsWith(dirUsuario)) {
            throw AccessDeniedToFileException()
        }

        val recurso = FileSystemResource(archivoPath)
        if (!recurso.exists()) {
            throw FileNotFoundException()
        }

        val contentType = when {
            filename.endsWith(".jpg") || filename.endsWith(".jpeg") -> MediaType.IMAGE_JPEG
            filename.endsWith(".png") -> MediaType.IMAGE_PNG
            else -> MediaType.APPLICATION_OCTET_STREAM
        }

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"${filename}\"")
            .contentType(contentType)
            .body(recurso)
    }
}
