package star.home.fortune.client

import com.google.genai.Client
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.GenerateContentResponse
import com.google.genai.types.Part
import org.springframework.stereotype.Component
import star.home.fortune.config.FortuneConfig
import star.member.constants.Constellation
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

@Component
class FortuneClient(private val config: FortuneConfig):
    CompletableFuture<GenerateContentResponse>() {
    private val client = Client.builder().apiKey(config.apiKey).build()

    suspend fun getFortune(constellation: Constellation, date: LocalDate): CompletableFuture<GenerateContentResponse?>? {
        val systemInstruction = Content.fromParts(Part.fromText(config.llmPrompt))
        val userContent = buildUserContent(constellation, date)
        val requestConfig = buildConfig(systemInstruction)

        return client.async.models.generateContent("gemini-2.5-flash", userContent, requestConfig)
    }

    private fun buildConfig(systemInstruction: Content): GenerateContentConfig {
        return GenerateContentConfig.builder()
            .systemInstruction(systemInstruction)
            .candidateCount(1)
            .systemInstruction(systemInstruction)
            .build()
    }

    private fun buildUserContent(constellation: Constellation, date: LocalDate): Content {
        val text =
            "${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일 ${constellation.koreanName}자리 운세를 알려줘"
        return Content.fromParts(Part.fromText(text))
    }


}