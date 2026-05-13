package com.unq.rapiempleo.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ValidationExceptionHandler {

    @ExceptionHandler(InvalidEmailException::class)
    fun handleInvalidEmailException(exc: InvalidEmailException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to "El email ingresado no es correcto"))
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(exc : UserNotFoundException) : ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to "Su usuario no esta disponible ahora mismo"))
    }

    @ExceptionHandler(InvalidPasswordException::class)
    fun handleInvalidPasswordException(exc : InvalidPasswordException) : ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to "Contraseña incorrecta"))
    }

    @ExceptionHandler(OfferNotFoundException::class)
    fun handleOfferNotFoundException(exc : OfferNotFoundException) : ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to "Sin acceso a la oferta solictada momentaneamente"))
    }

    @ExceptionHandler(UserNotAvailable::class)
    fun handleUserNotAvailable(exc : UserNotAvailable) : ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mapOf("message" to "Su usuario no esta habilitado para postularse"))
    }

    @ExceptionHandler(CvLimitExceededException::class)
    fun handleCvLimitExceededException(exc : CvLimitExceededException) : ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to exc.message!!))
    }

    @ExceptionHandler(PostulanteNotFoundException::class)
    fun handlePostulanteNotFoundException(exc : PostulanteNotFoundException) : ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to exc.message!!))
    }

    @ExceptionHandler(FileNotAllowedToUploadException::class)
    fun handleFileNotAllowedToUploadException(exc : FileNotAllowedToUploadException) : ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to exc.message!!))
    }

    @ExceptionHandler(FileNameNotAllowedException::class)
    fun handleFileNameNotAllowedException(exc : FileNameNotAllowedException) : ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to exc.message!!))
    }

    @ExceptionHandler(AccessDeniedToFileException::class)
    fun handleAccessDeniedToFileException(exc: AccessDeniedToFileException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mapOf("message" to exc.message!!))
    }

    @ExceptionHandler(FileNotFoundException::class)
    fun handleFileNotFoundException(exc: FileNotFoundException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to exc.message!!))
    }
}