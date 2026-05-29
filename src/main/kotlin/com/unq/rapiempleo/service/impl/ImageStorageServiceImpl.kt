package com.unq.rapiempleo.service.impl

import com.unq.rapiempleo.exceptions.FileNameNotAllowedException
import com.unq.rapiempleo.exceptions.ImageNotAllowedException
import com.unq.rapiempleo.service.ImageStorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class ImageStorageServiceImpl(
    @Value("\${app.upload.fotos.dir}") private val fotosDir: String
) : ImageStorageService {

    companion object {
        private const val MAX_SIZE_BYTES = 5L * 1024 * 1024
        private val ALLOWED_TYPES = setOf("image/jpeg", "image/png")
        private val JPEG_MAGIC = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte())
        private val PNG_MAGIC = byteArrayOf(
            0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte(),
            0x0D.toByte(), 0x0A.toByte(), 0x1A.toByte(), 0x0A.toByte()
        )
    }

    override fun guardarImagenPerfilPostulante(idPostulante: Long, archivo: MultipartFile): String {
        return guardarFoto(idPostulante, archivo, "postulante")
    }

    override fun guardarImagenPerfilOfertante(idOfertante: Long, archivo: MultipartFile): String {
        return guardarFoto(idOfertante, archivo, "ofertante")
    }

    private fun guardarFoto(id: Long, archivo: MultipartFile, tipo: String): String {
        if (archivo.contentType !in ALLOWED_TYPES) {
            throw ImageNotAllowedException()
        }

        if (archivo.size > MAX_SIZE_BYTES) {
            throw ImageNotAllowedException()
        }

        val extension = when (archivo.contentType) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            else -> throw ImageNotAllowedException()
        }

        val expectedMagic = if (extension == "jpg") JPEG_MAGIC else PNG_MAGIC
        archivo.inputStream.use { input ->
            val header = ByteArray(expectedMagic.size)
            val bytesLeidos = input.read(header)
            if (bytesLeidos < expectedMagic.size || !header.contentEquals(expectedMagic)) {
                throw ImageNotAllowedException()
            }
        }

        val dirUsuario = Paths.get(fotosDir, tipo, id.toString())
        if (!Files.exists(dirUsuario)) {
            Files.createDirectories(dirUsuario)
        }

        val nombreArchivo = "foto.$extension"
        val destino = dirUsuario.resolve(nombreArchivo)

        if (!destino.normalize().startsWith(dirUsuario.normalize())) {
            throw FileNameNotAllowedException()
        }

        Files.copy(archivo.inputStream, destino, StandardCopyOption.REPLACE_EXISTING)

        return "$tipo/$id/$nombreArchivo"
    }
}
