package com.unq.rapiempleo.controller

import com.unq.rapiempleo.TempEmpresaChecker
import com.unq.rapiempleo.dto.EmpresaCheckDTO
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Transactional
@RequestMapping
@RestController
class VerificationController (
    private val empresaChecker : TempEmpresaChecker
) {


    @PostMapping("/verificarEmpresa")
    fun verificacionDatosEmpresa(@RequestBody empresaData : EmpresaCheckDTO) : ResponseEntity<Map<String, String>>{
        empresaChecker.checkEmpresaRequestVerify(empresaData)
        return ResponseEntity(mapOf("message" to "Empresa verificada"), HttpStatus.OK)
    }
}