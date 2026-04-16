package com.unq.rapiempleo.repository

import com.unq.rapiempleo.model.Oferta
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface OfertaRepository : JpaRepository<Oferta, Long>{

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE oferta AUTO_INCREMENT = 1", nativeQuery = true)
    fun resetIdOferta()

    fun findByTituloContainingIgnoreCase(titulo : String) : List<Oferta>

    @Query("""
    SELECT o FROM Oferta o WHERE
    (:titulo IS NULL OR LOWER(o.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))) AND
    (:empresa IS NULL OR LOWER(o.empresa) LIKE LOWER(CONCAT('%', :empresa, '%'))) AND
    (:modalidad IS NULL OR o.modalidad = :modalidad) AND
    (:ubicacion IS NULL OR LOWER(o.ubicacion) LIKE LOWER(CONCAT('%', :ubicacion, '%')))
    """)
    fun buscarConFiltros(
        @Param("titulo") titulo: String?,
        @Param("empresa") empresa: String?,
        @Param("modalidad") modalidad: String?,
        @Param("ubicacion") ubicacion: String?
    ): List<Oferta>
}