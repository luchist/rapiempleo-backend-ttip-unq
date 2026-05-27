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

    @ExceptionHandler(OfertanteNotFoundException::class)
    fun handleOfertanteNotFoundException(exc : OfertanteNotFoundException) : ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to "Ofertante no encontrado"))
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

    @ExceptionHandler(NoCvAvailableException::class)
    fun handleNoCvAvailableException(exc: NoCvAvailableException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to exc.message!!))
    }

    @ExceptionHandler(CvNotFoundException::class)
    fun handleCvNotFoundException(exc: CvNotFoundException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to exc.message!!))
    }

    @ExceptionHandler(PostulanteAlreadyPostedOffer::class)
    fun handlePostulanteAlreadyPostedOfferException(exc: PostulanteAlreadyPostedOffer): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to exc.message!!))
    }

    @ExceptionHandler(PostulacionEstadoNotFoundException::class)
    fun handlePostulacionEstadoNotFoundException(exc: PostulacionEstadoNotFoundException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to exc.message!!))
    }

    @ExceptionHandler(AccessDeniedToPostulacionException::class)
    fun handleAccessDeniedToPostulacionException(exc: AccessDeniedToPostulacionException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mapOf("message" to exc.message!!))
    }

    @ExceptionHandler(UnauthenticatedException::class)
    fun handleUnauthenticatedException(exc: UnauthenticatedException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("message" to exc.message!!))
    }

    @ExceptionHandler(EstadoSinCambiosException::class)
    fun handleEstadoSinCambiosException(exc: EstadoSinCambiosException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.OK).body(mapOf("message" to exc.message!!))
    }
}