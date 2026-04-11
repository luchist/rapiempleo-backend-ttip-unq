package com.unq.rapiempleo.repository

import com.unq.rapiempleo.model.Postulante
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PostulanteRepository : JpaRepository<Postulante, Long> {

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE postulante AUTO_INCREMENT = 1", nativeQuery = true)
    fun resetIdPostulante()
}