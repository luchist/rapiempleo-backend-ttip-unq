package com.unq.rapiempleo.service.impl

import com.unq.rapiempleo.dto.AvisoPostulanteDTO
import com.unq.rapiempleo.dto.CvEntryRequestDTO
import com.unq.rapiempleo.dto.PostulacionBoardItemDTO
import com.unq.rapiempleo.dto.PostulanteDTO
import com.unq.rapiempleo.dto.PostulanteRegistryDTO
import com.unq.rapiempleo.exceptions.AccessDeniedToFileException
import com.unq.rapiempleo.exceptions.AccessDeniedToPostulacionException
import com.unq.rapiempleo.exceptions.OfferNotFoundException
import com.unq.rapiempleo.exceptions.CvLimitExceededException
import com.unq.rapiempleo.exceptions.CvNotFoundException
import com.unq.rapiempleo.exceptions.DuplicatedEmailException
import com.unq.rapiempleo.exceptions.EstadoSinCambiosException
import com.unq.rapiempleo.exceptions.UnauthenticatedException
import com.unq.rapiempleo.exceptions.NoCvAvailableException
import com.unq.rapiempleo.exceptions.OfertanteNotFoundException
import com.unq.rapiempleo.exceptions.PostulacionEstadoNotFoundException
import com.unq.rapiempleo.exceptions.PostulanteAlreadyPostedOffer
import com.unq.rapiempleo.exceptions.PostulanteNotFoundException
import com.unq.rapiempleo.model.CvEntry
import com.unq.rapiempleo.model.EstadoCvPostulado
import com.unq.rapiempleo.model.EstadoPostulacion
import com.unq.rapiempleo.model.NotificationEntry
import com.unq.rapiempleo.model.Oferta
import com.unq.rapiempleo.model.PostulacionCv
import com.unq.rapiempleo.model.PostulacionEstado
import com.unq.rapiempleo.model.Postulante
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.OfertanteRepository
import com.unq.rapiempleo.repository.PostulacionEstadoRepository
import com.unq.rapiempleo.repository.PostulanteRepository
import com.unq.rapiempleo.service.PostulanteService
import com.unq.rapiempleo.service.auxiliar.PostulationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class PostulanteServiceImpl (
    private val ofertaRepository: OfertaRepository,
    private val postulanteRepository: PostulanteRepository,
    private val postulacionEstadoRepository: PostulacionEstadoRepository,
    private val ofertanteRepository: OfertanteRepository,
    private val publisher : ApplicationEventPublisher,
    private val passwordEncoder: PasswordEncoder,
) : PostulanteService {

    override fun getPostulante(idPostulante: Long) : PostulanteDTO {
        val postulante = postulanteRepository.findById(idPostulante)
            .orElseThrow { PostulanteNotFoundException() }
        return PostulanteDTO.desdeModelo(postulante)
    }

    override fun postularEnOferta(idOferta: Long, idPostulante: Long) {
        val ofertaOpt = ofertaRepository.findById(idOferta)
            .orElseThrow{ throw OfferNotFoundException() }
        val postulanteop = postulanteRepository.findById(idPostulante)
            .orElseThrow { throw PostulanteNotFoundException() }

        if (postulanteop.cvEntries.isEmpty())
            throw NoCvAvailableException()

        if (yaEstaPostulado(postulanteop, ofertaOpt))
            throw PostulanteAlreadyPostedOffer()

        val cvAEnviar = postulanteop.cvEntries
            .find { it.cvPath == postulanteop.cvFavorito }
            ?: postulanteop.cvEntries[0]

        //ofertaOpt.postulantes.add(postulanteop)
        ofertaOpt.cvPostulantes.add(
            PostulacionCv(postulanteop.id_postulante!!,cvAEnviar.cvPath, ofertaOpt.id_oferta!!, EstadoCvPostulado.ESPERA))
        postulanteop.postulaciones.add(ofertaOpt)

        val postulacionEstado = PostulacionEstado(postulante = postulanteop, oferta = ofertaOpt, estado = EstadoPostulacion.Aplicado)
        postulacionEstadoRepository.save(postulacionEstado)

        ofertaRepository.save(ofertaOpt)
        postulanteRepository.save(postulanteop)

        publisher.publishEvent(
            PostulationEvent(
                ofertaOpt.ofertante!!.id_ofertante!!,
                ofertaOpt.titulo)
        )
    }

    fun yaEstaPostulado(postulante: Postulante, oferta: Oferta) : Boolean {
        val postulacionExistente = postulacionEstadoRepository.findByPostulante(postulante)
            .any { postulacion -> postulacion.oferta.id_oferta == oferta.id_oferta }

        return (postulacionExistente)
    }

    override fun getPreferencias(idPostulante: Long) : String {
        val postulante = postulanteRepository.findById(idPostulante)
            .orElseThrow { PostulanteNotFoundException() }
        return postulante.preferencias
    }

    override fun registrarUserPostulante(postulanteRegistro: PostulanteRegistryDTO) {
        val postulantePorEmail = postulanteRepository.findByEmail(postulanteRegistro.email)
        val ofertantePorEmail = ofertanteRepository.findByEmail(postulanteRegistro.email)
        if (postulantePorEmail != null || ofertantePorEmail != null) {
            throw DuplicatedEmailException()
        }

        val encodedPassword = passwordEncoder.encode(postulanteRegistro.password)
        val nuevoPostulante = Postulante(
            postulanteRegistro.name,
            "Estoy buscando trabajo como desarrollador, en la ciudad de Buenos Aires. Prefiero los trabajos con modalidad remota",
            postulanteRegistro.email,
            encodedPassword!!
        )
        postulanteRepository.save(nuevoPostulante)
    }

    override fun agregarCv(idPostulante: Long, cvPath: String) {
        val postulante = postulanteRepository.findById(idPostulante)
            .orElseThrow { PostulanteNotFoundException() }

        val idToSet = obtenerIdDePath(cvPath)
        if (postulante.id_postulante != idToSet) {
            throw AccessDeniedToFileException()
        }

        if (postulante.cvEntries.size >= 4) {
            throw CvLimitExceededException()
        }

        val esPrimerCv = postulante.cvEntries.isEmpty()
        if (esPrimerCv) {
            postulante.cvFavorito = cvPath
        }

        postulante.cvEntries.add(CvEntry(cvPath))

        postulanteRepository.save(postulante)
    }

    override fun setearCvFavorito(idPostulante: Long, cvPath: String) {
        val postulante = postulanteRepository.findById(idPostulante)
            .orElseThrow { PostulanteNotFoundException() }

        val idToSet = obtenerIdDePath(cvPath)
        if (postulante.id_postulante != idToSet) {
            throw AccessDeniedToFileException()
        }

        if (postulante.cvEntries.none { it.cvPath == cvPath }) {
            throw CvNotFoundException()
        }
        postulante.cvFavorito = cvPath
        postulanteRepository.save(postulante)
    }

    private fun obtenerIdDePath(cvPath: String): Long {
        val idPostulante = cvPath.split("/")[0]
        return idPostulante.toLong()
    }


    override fun eliminarNotificacion(idPostulante: Long, idNotificacion: Long) {
        val userToModify = postulanteRepository.findById(idPostulante).orElseThrow { throw OfertanteNotFoundException() }
        userToModify!!.eliminarNotificacionCvVisto(idNotificacion.toInt())

        postulanteRepository.save(userToModify)
    }

    override fun notificarAccionEnCv(avisoPostulacion: AvisoPostulanteDTO) {
        val ofertaPostulada = ofertaRepository.findById(avisoPostulacion.id_oferta)
            .orElseThrow { OfferNotFoundException() }
        val postulanteANotificar = postulanteRepository.findById(avisoPostulacion.id_postulante)
            .orElseThrow { PostulanteNotFoundException() }
        val postulacion = ofertaPostulada.cvPostulantes.find { postulacion -> postulacion.id_postulante == avisoPostulacion.id_postulante}

        if (postulacion!!.estadoCv == EstadoCvPostulado.ESPERA) {
            postulacion.estadoCv = EstadoCvPostulado.VISTO
            postulanteANotificar.notificacionesCv.add(NotificationEntry(avisoPostulacion.tipo_aviso,ofertaPostulada.titulo))
            ofertaRepository.save(ofertaPostulada)
            postulanteRepository.save(postulanteANotificar)
        } else if (avisoPostulacion.tipo_aviso != EstadoCvPostulado.VISTO) {
            postulacion.estadoCv = avisoPostulacion.tipo_aviso
            postulanteANotificar.notificacionesCv.add(NotificationEntry(avisoPostulacion.tipo_aviso,ofertaPostulada.titulo))
            ofertaRepository.save(ofertaPostulada)
            postulanteRepository.save(postulanteANotificar)
        }
    }

    override fun getBoard(idPostulante: Long) : List<PostulacionBoardItemDTO> {
        val postulante = postulanteRepository.findById(idPostulante)
            .orElseThrow { PostulanteNotFoundException() }

        val postulacionesEstado = postulacionEstadoRepository.findByPostulante(postulante)

        return postulacionesEstado.map {
            PostulacionBoardItemDTO.desdeModelo(it)
        }
    }

    override fun actualizarImagenPerfil(idPostulante: Long, imagePath: String) {
        val email = SecurityContextHolder.getContext().authentication?.name
            ?: throw UnauthenticatedException()

        val postulante = postulanteRepository.findByEmail(email)
            ?: throw PostulanteNotFoundException()

        if (postulante.id_postulante != idPostulante) {
            throw AccessDeniedToFileException()
        }

        postulante.fotoPerfil = imagePath
        postulanteRepository.save(postulante)
    }

    override fun getIdPorEmail(email: String): Long {
        val postulante = postulanteRepository.findByEmail(email)
            ?: throw PostulanteNotFoundException()
        return postulante.id_postulante!!
    }

    override fun agregarOfertaFavorita(idPostulante: Long, idOferta: Long) {
        val ofertaAFavoritos = ofertaRepository.findById(idOferta).orElseThrow { throw OfferNotFoundException() }
        val postulante = postulanteRepository.findById(idPostulante).orElseThrow { throw PostulanteNotFoundException() }

        postulante.favoritos.add(ofertaAFavoritos)
        postulanteRepository.save(postulante)
    }

    override fun removerOfertaFavorita(idPostulante: Long, idOferta: Long) {
        val ofertaASacar = ofertaRepository.findById(idOferta).orElseThrow { throw OfferNotFoundException() }
        val postulante = postulanteRepository.findById(idPostulante).orElseThrow { throw PostulanteNotFoundException() }

        postulante.favoritos.remove(ofertaASacar)
        postulanteRepository.save(postulante)
    }

    override fun removerCvIndicado(cvEntryReq : CvEntryRequestDTO) {
        val postulante = postulanteRepository.findById(cvEntryReq.idPostulante).orElseThrow { throw PostulanteNotFoundException() }

        if (postulante.cvFavorito == cvEntryReq.cvPath) {
            val postulanteModificado = this.removerYCambiarFavorito(postulante, cvEntryReq.cvPath)
            postulanteRepository.save(postulanteModificado)
        } else {
            postulante.cvEntries.removeIf{ cv -> cv.cvPath == cvEntryReq.cvPath }
            postulanteRepository.save(postulante)
        }
    }

    fun removerYCambiarFavorito(postulante : Postulante, cvName: String) : Postulante {
        if (postulante.cvEntries.size == 1) {
            postulante.cvEntries.removeFirst()
            return postulante
        } else {
            postulante.cvEntries.removeIf{ cv -> cv.cvPath == cvName }
            postulante.cvFavorito = postulante.cvEntries[0].cvPath
            return postulante
        }
    }

    override fun updateEstadoPostulacion(
        idPostulante: Long,
        idPostulacionEstado: Long,
        nuevoEstado: EstadoPostulacion
    ) {
        val email = SecurityContextHolder.getContext().authentication?.name
            ?: throw UnauthenticatedException()

        val postulante = postulanteRepository.findByEmail(email)
            ?: throw PostulanteNotFoundException()

        if (postulante.id_postulante != idPostulante) {
            throw AccessDeniedToPostulacionException()
        }

        val postulacionEstado = postulacionEstadoRepository.findById(idPostulacionEstado)
            .orElseThrow { PostulacionEstadoNotFoundException() }

        if (postulacionEstado.postulante.id_postulante != postulante.id_postulante) {
            throw AccessDeniedToPostulacionException()
        }

        if (postulacionEstado.estado == nuevoEstado)
            throw EstadoSinCambiosException()

        postulacionEstado.estado = nuevoEstado
        postulacionEstadoRepository.save(postulacionEstado)
    }
}