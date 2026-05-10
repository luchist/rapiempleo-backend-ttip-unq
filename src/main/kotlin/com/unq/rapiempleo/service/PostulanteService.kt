package com.unq.rapiempleo.service

import com.unq.rapiempleo.dto.PostulanteDTO
import com.unq.rapiempleo.dto.LoginResponseDTO
import com.unq.rapiempleo.dto.PostulanteRegistryDTO
import com.unq.rapiempleo.dto.UsuarioLoginDTO
import org.springframework.stereotype.Service

@Service
interface PostulanteService {

    fun getPostulante (idPostulante : Long) : PostulanteDTO
    fun postularEnOferta (idOferta : Long, idPostulante: Long)
    fun getPreferencias(idPostulante: Long) : String
    fun registrarUserPostulante(postulanteRegistro: PostulanteRegistryDTO)
    fun agregarCv(idPostulante: Long, cvPath: String)
}