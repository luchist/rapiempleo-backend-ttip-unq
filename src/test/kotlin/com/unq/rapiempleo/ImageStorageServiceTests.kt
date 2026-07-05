package com.unq.rapiempleo

import com.unq.rapiempleo.exceptions.ImageNotAllowedException
import com.unq.rapiempleo.service.impl.ImageStorageServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import org.springframework.mock.web.MockMultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ImageStorageServiceTests {

    @TempDir
    lateinit var tempDir: Path

    private val jpegMagic = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0x00)
    private val pngMagic = byteArrayOf(
        0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte(),
        0x0D.toByte(), 0x0A.toByte(), 0x1A.toByte(), 0x0A.toByte()
    )

    private fun service() = ImageStorageServiceImpl(tempDir.toString())

    @Test
    fun imagenConContentTypeInvalidoLanzaExcepcion() {
        val archivo = MockMultipartFile("archivo", "foto.gif", "image/gif", jpegMagic)
        assertThrows<ImageNotAllowedException> {
            service().guardarImagenPerfilPostulante(1, archivo)
        }
    }

    @Test
    fun imagenDemasiadoGrandeLanzaExcepcion() {
        val demasiadoGrande = ByteArray(5 * 1024 * 1024 + 1)
        val archivo = MockMultipartFile("archivo", "foto.jpg", "image/jpeg", demasiadoGrande)
        assertThrows<ImageNotAllowedException> {
            service().guardarImagenPerfilPostulante(1, archivo)
        }
    }

    @Test
    fun imagenConFirmaInvalidaLanzaExcepcion() {
        val archivo = MockMultipartFile("archivo", "foto.jpg", "image/jpeg", byteArrayOf(0x00, 0x01, 0x02))
        assertThrows<ImageNotAllowedException> {
            service().guardarImagenPerfilPostulante(1, archivo)
        }
    }

    @Test
    fun guardarImagenPostulanteValidaDevuelveLaRutaYGuardaElArchivo() {
        val archivo = MockMultipartFile("archivo", "foto.jpg", "image/jpeg", jpegMagic)

        val ruta = service().guardarImagenPerfilPostulante(1, archivo)

        Assertions.assertEquals("postulante/1/foto.jpg", ruta)
        Assertions.assertTrue(Files.exists(Paths.get(tempDir.toString(), "postulante", "1", "foto.jpg")))
    }

    @Test
    fun guardarImagenOfertanteValidaDevuelveLaRutaYGuardaElArchivo() {
        val archivo = MockMultipartFile("archivo", "foto.png", "image/png", pngMagic)

        val ruta = service().guardarImagenPerfilOfertante(2, archivo)

        Assertions.assertEquals("ofertante/2/foto.png", ruta)
        Assertions.assertTrue(Files.exists(Paths.get(tempDir.toString(), "ofertante", "2", "foto.png")))
    }
}
