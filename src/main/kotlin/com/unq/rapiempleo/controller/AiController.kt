package com.unq.rapiempleo.controller

import com.unq.rapiempleo.service.PostulanteService
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.google.genai.GoogleGenAiChatModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class AiController @Autowired constructor(
    private val chatModel: GoogleGenAiChatModel,
    @Autowired
    private var postulanteService : PostulanteService,
    private val chatInstructionsForSearchRecommendation: String =
        "You are a job search recommendation engine." +
        "Based on the user profile preferences provided, please give me a" +
        "valid search query with the following format:" +
        "'titulo: <job title>, ubicacion: <preferred city or country>, modalidad: <preferred work type, can be Local, Remoto, Hibrido>'" +
        "Your responses should ONLY contain the search query string without any greetings or extra text"
) {
    @GetMapping("/ai/context")
    fun context(
        @RequestParam(value = "message", defaultValue = "") message: String
    ) : Map<String, String> {

        // TODO reemplazar con el id del usuario cuando implentemos perfiles
        val context = postulanteService.getPreferencias(1)

        val userMessage = UserMessage.builder()
            .text(chatInstructionsForSearchRecommendation + "User context:" + context )
            .build()

        return mapOf("response" to chatModel.call(userMessage))
    }
}