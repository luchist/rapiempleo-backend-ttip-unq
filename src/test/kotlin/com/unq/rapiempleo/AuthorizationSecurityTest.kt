package com.unq.rapiempleo

import com.unq.rapiempleo.dto.OfertaCreateRequest
import com.unq.rapiempleo.dto.OfertanteRegistryDTO
import com.unq.rapiempleo.dto.PostulanteRegistryDTO
import com.unq.rapiempleo.model.Modalidad
import com.unq.rapiempleo.repository.OfertaRepository
import com.unq.rapiempleo.repository.OfertanteRepository
import com.unq.rapiempleo.repository.PostulacionEstadoRepository
import com.unq.rapiempleo.repository.PostulanteRepository
import com.unq.rapiempleo.security.JwtTokenProvider
import com.unq.rapiempleo.service.OfertanteService
import com.unq.rapiempleo.service.PostulanteService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * End-to-end authorization tests through the real security filter chain: a real JWT is generated
 * per user (so [com.unq.rapiempleo.security.JwtAuthenticationFilter] populates the principal id) and
 * the endpoints protected by `@PreAuthorize` are exercised as owner / non-owner / wrong-role.
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthorizationSecurityTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var postulanteService: PostulanteService
    @Autowired private lateinit var ofertanteService: OfertanteService
    @Autowired private lateinit var jwtTokenProvider: JwtTokenProvider
    @Autowired private lateinit var postulanteRepository: PostulanteRepository
    @Autowired private lateinit var ofertanteRepository: OfertanteRepository
    @Autowired private lateinit var ofertaRepository: OfertaRepository
    @Autowired private lateinit var postulacionEstadoRepository: PostulacionEstadoRepository

    // Tokens: generateToken(email, userId, typeUser); typeUser=true -> POSTULANTE, false -> OFERTANTE.
    private fun tokenPostulante1() = jwtTokenProvider.generateToken("p1@test.com", 1, true)
    private fun tokenPostulante2() = jwtTokenProvider.generateToken("p2@test.com", 2, true)
    private fun tokenOfertante1() = jwtTokenProvider.generateToken("e1@test.com", 1, false)
    private fun tokenOfertante2() = jwtTokenProvider.generateToken("e2@test.com", 2, false)

    @BeforeEach
    fun setup() {
        postulanteService.registrarUserPostulante(PostulanteRegistryDTO("Postulante Uno", "p1@test.com", "passpass"))
        postulanteService.registrarUserPostulante(PostulanteRegistryDTO("Postulante Dos", "p2@test.com", "passpass"))
        ofertanteService.registroOfertante(OfertanteRegistryDTO("Empresa Uno", "Tech1", "e1@test.com", "passpass"))
        ofertanteService.registroOfertante(OfertanteRegistryDTO("Empresa Dos", "Tech2", "e2@test.com", "passpass"))
        ofertanteService.crearOferta(
            1,
            OfertaCreateRequest("Dev", "Desc", Modalidad.Remoto, 1000, 2000, "CABA")
        )
    }

    @AfterEach
    fun cleanUp() {
        postulacionEstadoRepository.deleteAll()
        postulacionEstadoRepository.resetIdPostulacionEstado()
        ofertaRepository.deleteAll()
        ofertaRepository.resetIdOferta()
        postulanteRepository.deleteAll()
        postulanteRepository.resetIdPostulante()
        ofertanteRepository.deleteAll()
        ofertanteRepository.resetIdOfertante()
    }

    // ---- esUsuarioActual on a path variable: GET /postulante/{id}/board ----

    @Test
    fun boardDelPropioPostulanteEsPermitido() {
        mockMvc.perform(get("/postulante/1/board").header("Authorization", "Bearer ${tokenPostulante1()}"))
            .andExpect(status().isOk)
    }

    @Test
    fun boardDeOtroPostulanteEsProhibido() {
        mockMvc.perform(get("/postulante/1/board").header("Authorization", "Bearer ${tokenPostulante2()}"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun boardConRolOfertanteEsProhibido() {
        mockMvc.perform(get("/postulante/1/board").header("Authorization", "Bearer ${tokenOfertante1()}"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun boardSinTokenEsRechazado() {
        mockMvc.perform(get("/postulante/1/board"))
            .andExpect(status().is4xxClientError)
    }

    // ---- esUsuarioActual on a path variable: GET /postulante/{id} ----

    @Test
    fun perfilDelPropioPostulanteEsPermitido() {
        mockMvc.perform(get("/postulante/1").header("Authorization", "Bearer ${tokenPostulante1()}"))
            .andExpect(status().isOk)
    }

    @Test
    fun perfilDeOtroPostulanteEsProhibido() {
        mockMvc.perform(get("/postulante/1").header("Authorization", "Bearer ${tokenPostulante2()}"))
            .andExpect(status().isForbidden)
    }

    // ---- esUsuarioActual on a path variable: GET /ofertante/{id} ----

    @Test
    fun perfilDelPropioOfertanteEsPermitido() {
        mockMvc.perform(get("/ofertante/1").header("Authorization", "Bearer ${tokenOfertante1()}"))
            .andExpect(status().isOk)
    }

    @Test
    fun perfilDeOtroOfertanteEsProhibido() {
        mockMvc.perform(get("/ofertante/1").header("Authorization", "Bearer ${tokenOfertante2()}"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun perfilOfertanteConRolPostulanteEsProhibido() {
        mockMvc.perform(get("/ofertante/1").header("Authorization", "Bearer ${tokenPostulante1()}"))
            .andExpect(status().isForbidden)
    }

    // ---- esUsuarioActual on a DTO field: DELETE /postulante/removeCV ----

    @Test
    fun removerCvDeOtroPostulanteEsProhibido() {
        val body = """{"idPostulante":1,"cvPath":"1/cv.pdf"}"""
        mockMvc.perform(
            delete("/postulante/removeCV")
                .header("Authorization", "Bearer ${tokenPostulante2()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isForbidden)
    }

    // ---- esUsuarioActual on a DTO field: POST /ofertante/saveCV ----

    @Test
    fun guardarCvComoOtroOfertanteEsProhibido() {
        val body = """{"idOfertante":1,"idPostulante":1,"cvPath":"1/cv.pdf"}"""
        mockMvc.perform(
            post("/ofertante/saveCV")
                .header("Authorization", "Bearer ${tokenOfertante2()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isForbidden)
    }

    // ---- gestionaOferta (owner derived from the offer): DELETE /ofertante/deletePostulationCV ----

    @Test
    fun eliminarCvDePostulacionEnOfertaAjenaEsProhibido() {
        val body = """{"idPostulante":1,"idOferta":1}"""
        mockMvc.perform(
            delete("/ofertante/deletePostulationCV")
                .header("Authorization", "Bearer ${tokenOfertante2()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isForbidden)
    }

    @Test
    fun eliminarCvDePostulacionConRolPostulanteEsProhibido() {
        val body = """{"idPostulante":1,"idOferta":1}"""
        mockMvc.perform(
            delete("/ofertante/deletePostulationCV")
                .header("Authorization", "Bearer ${tokenPostulante1()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isForbidden)
    }

    // ---- gestionaOferta + role: POST /postulante/respuestaCV (an employer action) ----

    @Test
    fun responderCvConRolPostulanteEsProhibido() {
        val body = """{"id_postulante":1,"id_oferta":1,"tipo_aviso":"VISTO"}"""
        mockMvc.perform(
            post("/postulante/respuestaCV")
                .header("Authorization", "Bearer ${tokenPostulante1()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isForbidden)
    }

    @Test
    fun responderCvSobreOfertaAjenaEsProhibido() {
        val body = """{"id_postulante":1,"id_oferta":1,"tipo_aviso":"VISTO"}"""
        mockMvc.perform(
            post("/postulante/respuestaCV")
                .header("Authorization", "Bearer ${tokenOfertante2()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isForbidden)
    }
}
