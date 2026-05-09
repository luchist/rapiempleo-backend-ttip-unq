package com.unq.rapiempleo.service

import org.springframework.web.multipart.MultipartFile

interface CvStorageService {
    fun guardarCv(idPostulante: Long, archivo: MultipartFile): String
}
