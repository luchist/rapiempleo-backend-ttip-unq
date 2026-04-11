package com.unq.rapiempleo.repository

import com.unq.rapiempleo.model.Oferta
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface OfertaRepository : JpaRepository<Oferta, Long>{

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE oferta AUTO_INCREMENT = 1", nativeQuery = true)
    fun resetIdOferta()

    fun findByTituloContainingIgnoreCase(titulo : String) : List<Oferta>
}