package com.unq.rapiempleo.service.impl

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

        val dirPostulante = Paths.get(uploadDir, idPostulante.toString())
        if (!Files.exists(dirPostulante)) {
            Files.createDirectories(dirPostulante)
        }

        val nombreArchivo = archivo.originalFilename ?: "cv.pdf"
        val destino = dirPostulante.resolve(nombreArchivo)
        Files.copy(archivo.inputStream, destino, StandardCopyOption.REPLACE_EXISTING)

        return "$uploadDir/$idPostulante/$nombreArchivo"
    }
}
