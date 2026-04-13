package com.unq.rapiempleo.repository

import com.unq.rapiempleo.model.Ofertante
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface OfertanteRepository : JpaRepository<Ofertante, Long> {

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE ofertante AUTO_INCREMENT = 1", nativeQuery = true)
    fun resetIdOfertante()

}

