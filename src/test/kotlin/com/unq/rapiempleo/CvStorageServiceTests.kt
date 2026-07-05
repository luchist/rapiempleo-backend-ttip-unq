package com.unq.rapiempleo

import com.unq.rapiempleo.exceptions.FileNameNotAllowedException
import com.unq.rapiempleo.exceptions.FileNotAllowedToUploadException
import com.unq.rapiempleo.service.impl.CvStorageServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import org.springframework.mock.web.MockMultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class CvStorageServiceTests {

    @TempDir
    lateinit var tempDir: Path

    private fun service() = CvStorageServiceImpl(tempDir.toString())

    @Test
    fun archivoConContentTypeInvalidoLanzaExcepcion() {
        val archivo = MockMultipartFile("archivo", "cv.txt", "text/plain", "hola".toByteArray())
        assertThrows<FileNotAllowedToUploadException> {
            service().guardarCv(1, archivo)
        }
    }

    @Test
    fun archivoSinFirmaPdfLanzaExcepcion() {
        val archivo = MockMultipartFile("archivo", "cv.pdf", "application/pdf", "NO ES UN PDF".toByteArray())
        assertThrows<FileNotAllowedToUploadException> {
            service().guardarCv(1, archivo)
        }
    }

    @Test
    fun archivoPdfMasCortoQueLaFirmaLanzaExcepcion() {
        val archivo = MockMultipartFile("archivo", "cv.pdf", "application/pdf", "%PD".toByteArray())
        assertThrows<FileNotAllowedToUploadException> {
            service().guardarCv(1, archivo)
        }
    }

    @Test
    fun archivoPdfValidoConNombreNoPdfLanzaExcepcion() {
        val archivo = MockMultipartFile("archivo", "cv.txt", "application/pdf", "%PDF-1.4 contenido".toByteArray())
        assertThrows<FileNameNotAllowedException> {
            service().guardarCv(1, archivo)
        }
    }

    @Test
    fun guardarCvValidoDevuelveLaRutaYGuardaElArchivo() {
        val contenido = "%PDF-1.4 contenido del cv".toByteArray()
        val archivo = MockMultipartFile("archivo", "cv.pdf", "application/pdf", contenido)

        val ruta = service().guardarCv(1, archivo)

        Assertions.assertEquals("1/cv.pdf", ruta)
        Assertions.assertTrue(Files.exists(Paths.get(tempDir.toString(), "1", "cv.pdf")))
    }
}
