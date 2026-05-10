package com.unq.rapiempleo.service.impl

import com.unq.rapiempleo.dto.LoginResponseDTO
import com.unq.rapiempleo.dto.UsuarioLoginDTO
import com.unq.rapiempleo.exceptions.InvalidEmailException
import com.unq.rapiempleo.exceptions.InvalidPasswordException
import com.unq.rapiempleo.repository.OfertanteRepository
import com.unq.rapiempleo.repository.PostulanteRepository
import com.unq.rapiempleo.security.JwtTokenProvider
import com.unq.rapiempleo.service.LoginService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class LoginServiceImpl(
    private val ofertanteRepository: OfertanteRepository,
    private val postulanteRepository: PostulanteRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) : LoginService {

    override fun loginDeUser(usuarioLoginDTO: UsuarioLoginDTO): LoginResponseDTO {
        val token = jwtTokenProvider.generateToken(usuarioLoginDTO.email)

        val userPos = postulanteRepository.findByEmail(usuarioLoginDTO.email)
        if (userPos == null) {
            val userOfer = ofertanteRepository.findByEmail(usuarioLoginDTO.email) ?: throw InvalidEmailException()
            this.passWordChecking(usuarioLoginDTO.password, userOfer.password)

            return LoginResponseDTO(userOfer.id_ofertante!!, userOfer.nombreOfertante, false, token)
        } else {
            this.passWordChecking(usuarioLoginDTO.password, userPos.password)

            return LoginResponseDTO(userPos.id_postulante!!, userPos.nombrPostulante, true, token)
        }
    }

    fun passWordChecking (passReceived : String, passRecovered : String) {
        if(!passwordEncoder.matches(passReceived, passRecovered)) {
            throw InvalidPasswordException()
        }
    }

}