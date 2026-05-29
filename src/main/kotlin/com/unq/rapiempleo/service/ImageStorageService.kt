package com.unq.rapiempleo.service

import org.springframework.web.multipart.MultipartFile

interface ImageStorageService {
    fun guardarImagenPerfilPostulante(idPostulante: Long, archivo: MultipartFile): String
    fun guardarImagenPerfilOfertante(idOfertante: Long, archivo: MultipartFile): String
}
