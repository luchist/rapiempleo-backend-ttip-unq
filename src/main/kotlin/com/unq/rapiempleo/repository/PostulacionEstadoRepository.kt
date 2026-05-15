package com.unq.rapiempleo.repository

import com.unq.rapiempleo.model.PostulacionEstado
import com.unq.rapiempleo.model.Postulante
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface PostulacionEstadoRepository : JpaRepository<PostulacionEstado, Long> {
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE oferta AUTO_INCREMENT = 1", nativeQuery = true)
    fun resetIdPostulacionEstado()

    fun findByPostulante(postulante: Postulante): List<PostulacionEstado>
}