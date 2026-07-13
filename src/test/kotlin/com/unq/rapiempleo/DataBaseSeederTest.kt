package com.unq.rapiempleo

import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import com.unq.rapiempleo.model.EstadoPostulacion
import com.unq.rapiempleo.model.EstadoOferta
import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.model.Oferta
import com.unq.rapiempleo.model.Ofertante
import com.unq.rapiempleo.model.PostulacionEstado
import com.unq.rapiempleo.model.Postulante
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.OfertanteRepository
import com.unq.rapiempleo.repository.PostulacionEstadoRepository
import com.unq.rapiempleo.repository.PostulanteRepository
import com.unq.rapiempleo.service.OfertanteService
import com.unq.rapiempleo.service.PostulanteService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Optional

class DataBaseSeederTest {

    private val postulanteRepository = mock<PostulanteRepository>()
    private val ofertaRepository = mock<OfertaRepository>()
    private val ofertanteRepository = mock<OfertanteRepository>()
    private val ofertanteService = mock<OfertanteService>()
    private val postulanteService = mock<PostulanteService>()
    private val postulacionEstadoRepository = mock<PostulacionEstadoRepository>()

    private val seeder = DataBaseSeeder(
        postulanteRepository, ofertaRepository, ofertanteRepository,
        ofertanteService, postulanteService, postulacionEstadoRepository
    )

    private val ofertante1 = Ofertante("Albert Wesker", "Electro Smart", "wesker8180@gmail.com", "passpass")
        .also { it.id_ofertante = 1L }
    private val ofertante2 = Ofertante("Ramon Salazar", "PixelLab", "salazar_ram@gmail.com", "wordpass")
        .also { it.id_ofertante = 2L }
    private val ofertante3 = Ofertante("Jack Baker", "Tech.Inc", "baker_jack7@gmail.com", "wordpass")
        .also { it.id_ofertante = 3L }
    private val leon = Postulante(
        "Leon Kennedy",
        "Estoy buscando trabajo como desarrollador",
        "leon0126@gmail.com",
        "passpass"
    ).also { it.id_postulante = 1L }
    private val ofertaEntrevistando = Oferta(
        "Backend Engineer", "CloudSync", "desc",
        Modalidad.Remoto, EstadoOferta.Abierto, 47000, 54000, "Ciudad de México"
    ).also { it.id_oferta = 6L }
    private val ofertaCerrada = Oferta(
        "Contador Sr", "Tepago SA", "desc",
        Modalidad.Presencial, EstadoOferta.Cerrado, 40000, 44000, "Temperley"
    ).also { it.id_oferta = 2L }

    @BeforeEach
    fun setupMocks() {
        whenever(ofertanteRepository.findById(1L)).thenReturn(Optional.of(ofertante1))
        whenever(ofertanteRepository.findById(2L)).thenReturn(Optional.of(ofertante2))
        whenever(ofertanteRepository.findById(3L)).thenReturn(Optional.of(ofertante3))
        whenever(postulanteRepository.findById(1L)).thenReturn(Optional.of(leon))
        whenever(ofertaRepository.findById(6L)).thenReturn(Optional.of(ofertaEntrevistando))
        whenever(ofertaRepository.findById(2L)).thenReturn(Optional.of(ofertaCerrada))
        whenever(postulacionEstadoRepository.findByPostulante(leon)).thenReturn(listOf(
            PostulacionEstado(oferta = ofertaEntrevistando, postulante = leon, estado = EstadoPostulacion.Aplicado),
            PostulacionEstado(oferta = ofertaCerrada, postulante = leon, estado = EstadoPostulacion.Aplicado)
        ))
    }

    @Test
    fun seedLimpiaYResetearTodosLosRepositorios() {
        seeder.seed()

        verify(postulacionEstadoRepository).deleteAll()
        verify(ofertanteRepository).deleteAll()
        verify(postulanteRepository).deleteAll()
        verify(ofertaRepository).deleteAll()
        verify(postulanteRepository).resetIdPostulante()
        verify(ofertaRepository).resetIdOferta()
        verify(ofertanteRepository).resetIdOfertante()
        verify(postulacionEstadoRepository).resetIdPostulacionEstado()
    }

    @Test
    fun seedRegistraTresOfertantesConLosDatosCorrectos() {
        val captor = argumentCaptor<OfertanteRegistryDTO>()

        seeder.seed()

        verify(ofertanteService, times(3)).registroOfertante(captor.capture())
        val nombres = captor.allValues.map { it.name }
        assertTrue(nombres.contains("Albert Wesker"))
        assertTrue(nombres.contains("Ramon Salazar"))
        assertTrue(nombres.contains("Jack Baker"))
    }

    @Test
    fun seedGuardaOncePuestosDeTrabajoConOfertantesAsignados() {
        val captor = argumentCaptor<Iterable<Oferta>>()

        seeder.seed()

        verify(ofertaRepository).saveAll(captor.capture())
        val ofertas = captor.firstValue.toList()
        assertEquals(14, ofertas.size)
        assertEquals(ofertante3, ofertas[0].ofertante)  // Desarrollador Sr Full Stack -> Tech.Inc
        assertEquals(ofertante1, ofertas[2].ofertante)  // Desarrollador FrontEnd -> Electro Smart
        assertEquals(ofertante2, ofertas[7].ofertante)  // UX Designer -> PixelLab
    }

    @Test
    fun seedConfiguraElPerfilDeLeon() {
        seeder.seed()

        assertEquals("postulante/1/foto.jpg", leon.fotoPerfil)
        assertEquals(1, leon.cvEntries.size)
        assertEquals("1/leon-kennedy-cv-english.pdf", leon.cvEntries[0].cvPath)
        assertEquals("1/leon-kennedy-cv-english.pdf", leon.cvFavorito)
        verify(postulanteRepository).save(any())
    }

    @Test
    fun seedPostulaALeonEnLasOfertasCorrectas() {
        seeder.seed()

        verify(postulanteService).postularEnOferta(6L, leon.id_postulante!!)
        verify(postulanteService).postularEnOferta(2L, leon.id_postulante!!)
    }

    @Test
    fun seedActualizaLosEstadosDeLasPostulaciones() {
        val estadoEntrevistando = PostulacionEstado(
            oferta = ofertaEntrevistando, postulante = leon, estado = EstadoPostulacion.Aplicado
        )
        val estadoCerrado = PostulacionEstado(
            oferta = ofertaCerrada, postulante = leon, estado = EstadoPostulacion.Aplicado
        )

        whenever(postulacionEstadoRepository.findByPostulante(leon))
            .thenReturn(listOf(estadoEntrevistando, estadoCerrado))

        seeder.seed()

        assertEquals(EstadoPostulacion.Entrevistando, estadoEntrevistando.estado)
        assertEquals(EstadoPostulacion.Cerrado, estadoCerrado.estado)
        verify(postulacionEstadoRepository, times(2)).save(any())
    }
}
