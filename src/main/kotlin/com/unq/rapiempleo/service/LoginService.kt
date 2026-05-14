package com.unq.rapiempleo.service

import com.unq.rapiempleo.dto.LoginResponseDTO
import com.unq.rapiempleo.dto.UsuarioLoginDTO
import org.springframework.stereotype.Service

@Service
interface LoginService {
    fun loginDeUser (usuarioLoginDTO: UsuarioLoginDTO) : LoginResponseDTO
}