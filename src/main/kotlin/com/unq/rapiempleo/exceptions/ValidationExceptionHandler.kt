package com.unq.rapiempleo.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ValidationExceptionHandler {

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(exc : UserNotFoundException) : ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to "El email ingresado no es correcto"))
    }

    @ExceptionHandler(InvalidPasswordException::class)
    fun handleInvalidPasswordException(exc : InvalidPasswordException) : ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to "Contraseña inválida"))
    }

    @ExceptionHandler(CvLimitExceededException::class)
    fun handleCvLimitExceededException(exc : CvLimitExceededException) : ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to exc.message!!))
    }

    @ExceptionHandler(PostulanteNotFoundException::class)
    fun handlePostulanteNotFoundException(exc : PostulanteNotFoundException) : ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to exc.message!!))
    }
}