package com.unq.rapiempleo.controller

import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.google.genai.GoogleGenAiChatModel
import org.springframework.ai.google.genai.GoogleGenAiChatOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class AiController @Autowired constructor(
    private val chatModel: GoogleGenAiChatModel,
    private val chatInstructionsForSearchRecommendation: String =
        "You are a job search recommendation engine." +
        "Based on the user profile preferences provided, please give me a" +
        "valid search query with the following format:" +
        "'titulo: <job title>, ubicacion: <preferred city or country>, modalidad: <preferred work type, can be Local, Remoto, Hibrido>'" +
        "Your responses should ONLY contain the search query string without any greetings or extra text"
) {
//    @GetMapping("/ai/generate")
//    fun generate(
//        @RequestParam(value = "message", defaultValue = "Tell me a joke") message: String
//    ): Map<String, String> {
//        return mapOf("response" to chatModel.call(message))
//    }

//    @GetMapping("/ai/complex")
//    fun complex(
//        @RequestParam(value = "message", defaultValue = "") message: String
//    ): Map<String, String> {
//
//        var response = chatModel.call(
//            Prompt(
//                "Give me 5 pirate names.",
//                GoogleGenAiChatOptions.builder()
//                    .model("gemini-2.5-flash")
//                    .build()
//            )
//        )
//        return mapOf("response" to response.result.output.text.toString())
//    }

    @GetMapping("/ai/context")
    fun context(
        @RequestParam(value = "message", defaultValue = "") message: String
    ) : Map<String, String> {
        val context = "Estoy buscando trabajo como desarrollador, en la ciudad de Buenos Aires. Prefiero los trabajos con modalidad remota"

        val userMessage = UserMessage.builder()
            .text(chatInstructionsForSearchRecommendation + "User context:" + context )
            .build()

        return mapOf("response" to chatModel.call(userMessage))

    }
}