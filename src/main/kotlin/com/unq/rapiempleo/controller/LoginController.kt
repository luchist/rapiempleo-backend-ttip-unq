package com.unq.rapiempleo.controller

import com.unq.rapiempleo.dto.LoginResponseDTO
import com.unq.rapiempleo.dto.UsuarioLoginDTO
import com.unq.rapiempleo.service.LoginService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Transactional
@RequestMapping()
@RestController
class LoginController {

    @Autowired
    private lateinit var loginService: LoginService

    @PostMapping("/login")
    fun loginUser(@RequestBody loginData : UsuarioLoginDTO) : ResponseEntity<LoginResponseDTO> {
        val resUser : LoginResponseDTO = loginService.loginDeUser(loginData)
        return ResponseEntity(resUser,HttpStatus.OK)
    }
}