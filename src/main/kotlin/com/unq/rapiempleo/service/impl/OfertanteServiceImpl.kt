package com.unq.rapiempleo.service.impl


import com.unq.rapiempleo.dto.LoginResponseDTO
import com.unq.rapiempleo.dto.OfertanteDTO
import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import com.unq.rapiempleo.dto.UsuarioLoginDTO
import com.unq.rapiempleo.exceptions.InvalidPasswordException
import com.unq.rapiempleo.exceptions.UserNotFoundException
import com.unq.rapiempleo.model.Ofertante
import com.unq.rapiempleo.repository.OfertanteRepository
import com.unq.rapiempleo.security.JwtTokenProvider
import com.unq.rapiempleo.service.OfertanteService
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class OfertanteServiceImpl (
    private val ofertanteRepository: OfertanteRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) : OfertanteService{

    @Transactional
    override fun recuperarOfertante(idOfertante: Long): OfertanteDTO {
        val ofertante = ofertanteRepository.findById(idOfertante).orElseThrow { throw NullPointerException("No existe ese ofertante") }
        return OfertanteDTO.desdeModelo(ofertante)
    }

    override fun registroOfertante(ofertanteRegistro: OfertanteRegistryDTO) {
        val encodedPassword = passwordEncoder.encode(ofertanteRegistro.password)

        val nuevoOfertante = Ofertante(ofertanteRegistro.nombre,
            ofertanteRegistro.empresa,
            ofertanteRegistro.email,
            encodedPassword!!)
        ofertanteRepository.save(nuevoOfertante)
    }

    override fun loginOfertante(usuarioLoginData: UsuarioLoginDTO): LoginResponseDTO {
        val user = ofertanteRepository.findByEmail(usuarioLoginData.email) ?: throw UserNotFoundException("El email ingresado no es válido")

        if (!passwordEncoder.matches(usuarioLoginData.password, user.password)) {
            throw InvalidPasswordException("La contraseña es incorrecta")
        }
        val token = jwtTokenProvider.generateToken(user.email)
        return LoginResponseDTO(user.id_ofertante!!, user.nombreOfertante, false, token)
    }

}