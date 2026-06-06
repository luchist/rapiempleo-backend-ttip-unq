package com.unq.rapiempleo.service

import com.unq.rapiempleo.dto.AvisoPostulanteDTO
import com.unq.rapiempleo.dto.PostulacionBoardItemDTO
import com.unq.rapiempleo.dto.PostulanteDTO
import com.unq.rapiempleo.dto.PostulanteRegistryDTO
import com.unq.rapiempleo.model.EstadoPostulacion
import org.springframework.stereotype.Service

@Service
interface PostulanteService {

    fun getPostulante (idPostulante : Long) : PostulanteDTO
    fun postularEnOferta (idOferta : Long, idPostulante: Long)
    fun getPreferencias(idPostulante: Long) : String
    fun registrarUserPostulante(postulanteRegistro: PostulanteRegistryDTO)
    fun agregarCv(idPostulante: Long, cvPath: String)
    fun setearCvFavorito(idPostulante: Long, cvPath: String)
    fun getBoard(idPostulante: Long) : List<PostulacionBoardItemDTO>
    fun eliminarNotificacion (idPostulante : Long, idNotificacion: Long)
    fun notificarAccionEnCv(avisoPostulacion: AvisoPostulanteDTO)
    fun updateEstadoPostulacion(idPostulante: Long, idPostulacionEstado: Long, nuevoEstado: EstadoPostulacion)
    fun actualizarImagenPerfil(idPostulante: Long, imagePath: String)
    fun getIdPorEmail(email: String): Long
    fun agregarOfertaFavorita(idPostulante: Long, idOferta: Long)
    fun removerOfertaFavorita(idPostulante: Long, idOferta: Long)
}