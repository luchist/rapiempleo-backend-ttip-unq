package com.unq.rapiempleo.controller

import com.unq.rapiempleo.exceptions.UnauthenticatedException
import com.unq.rapiempleo.security.UsuarioAutenticado
import com.unq.rapiempleo.service.PostulanteService
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.google.genai.GoogleGenAiChatModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class AiController @Autowired constructor(
    private val chatModel: GoogleGenAiChatModel,
    @Autowired
    private var postulanteService : PostulanteService,
    private val chatInstructionsForSearchRecommendation: String =
        "You are a job search recommendation engine. " +
        "Based on the user profile preferences provided, return a short free-text job-search query " +
        "using only keywords (role, location, work type) as plain words separated by spaces. " +
        "Your responses should ONLY contain the query text, without any labels, greetings, or extra text"
) {
    @GetMapping("/ai/context")
    fun context(
        @RequestParam(value = "message", defaultValue = "") message: String,
        @AuthenticationPrincipal usuario: UsuarioAutenticado?
    ) : Map<String, String> {
        val idPostulante = usuario?.id ?: throw UnauthenticatedException()

        val context = postulanteService.getPreferencias(idPostulante)

        val userMessage = UserMessage.builder()
            .text(chatInstructionsForSearchRecommendation + "User context:" + context )
            .build()

        return mapOf("response" to chatModel.call(userMessage))
    }
}