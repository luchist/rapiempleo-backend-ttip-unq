package com.unq.rapiempleo.service

import com.unq.rapiempleo.dto.PostulanteDTO
import org.springframework.stereotype.Service

@Service
interface PostulanteService {

    fun getPostulante (idPostulante : Long) : PostulanteDTO
    fun postularEnOferta (idOferta : Long, idPostulante: Long)
    fun getPreferencias(idPostulante: Long) : String

}