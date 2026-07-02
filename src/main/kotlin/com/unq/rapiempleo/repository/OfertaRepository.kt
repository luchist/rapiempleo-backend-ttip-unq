package com.unq.rapiempleo.repository

import com.unq.rapiempleo.model.EstadoOferta
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

    fun findByEstado(estado: EstadoOferta): List<Oferta>

    fun findByTituloContainingIgnoreCaseAndEstado(titulo: String, estado: EstadoOferta): List<Oferta>

    // Full-text search over open offers ranked by relevance.
    // Backed by the FULLTEXT index ft_oferta the MATCH() column list must match it exactly.
    // estado is @Enumerated(STRING) so it is compared to the literal in this native query.
    @Query(value = """
    SELECT * FROM oferta
    WHERE estado = 'Abierto'
      AND MATCH(titulo, empresa, descripcion, ubicacion) AGAINST (:q IN BOOLEAN MODE)
    ORDER BY MATCH(titulo, empresa, descripcion, ubicacion) AGAINST (:q IN BOOLEAN MODE) DESC
    """, nativeQuery = true)
    fun busquedaInteligente(@Param("q") q: String): List<Oferta>

    @Query("""
    SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END
    FROM Oferta o JOIN o.cvPostulantes cv
    WHERE o.ofertante.id_ofertante = :idOfertante
    AND cv.id_postulante = :idPostulante
    """)
    fun existePostulanteEnOfertasDeOfertante(
        @Param("idOfertante") idOfertante: Long,
        @Param("idPostulante") idPostulante: Long
    ): Boolean
}