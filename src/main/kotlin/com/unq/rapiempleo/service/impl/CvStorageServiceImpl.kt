package com.unq.rapiempleo.service.impl

import com.unq.rapiempleo.exceptions.FileNameNotAllowedException
import com.unq.rapiempleo.exceptions.FileNotAllowedToUploadException
import com.unq.rapiempleo.service.CvStorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class CvStorageServiceImpl(
    @Value("\${app.upload.dir}") private val uploadDir: String
) : CvStorageService {

    override fun guardarCv(idPostulante: Long, archivo: MultipartFile): String {
        if (archivo.contentType != "application/pdf") {
            throw FileNotAllowedToUploadException()
        }

        val nombreArchivo = Paths.get(archivo.originalFilename ?: "cv.pdf").fileName.toString()
        if (!nombreArchivo.lowercase().endsWith(".pdf")) {
            throw FileNameNotAllowedException()
        }

        val dirPostulante = Paths.get(uploadDir, idPostulante.toString())
        if (!Files.exists(dirPostulante)) {
            Files.createDirectories(dirPostulante)
        }

        val destino = dirPostulante.resolve(nombreArchivo)

        if (!destino.normalize().startsWith(dirPostulante.normalize())) {
            throw FileNameNotAllowedException()
        }

        Files.copy(archivo.inputStream, destino, StandardCopyOption.REPLACE_EXISTING)

        return "$uploadDir/$idPostulante/$nombreArchivo"
    }
}
