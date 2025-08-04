package star.home.fortune.config

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.io.ResourceLoader
import java.nio.charset.StandardCharsets

@ConfigurationProperties(prefix = "fortune")
data class FortuneConfig(
    val apiKey: String,
    var llmPrompt: String,
) {
    @Autowired
    lateinit var resourceLoader: ResourceLoader

    @PostConstruct
    fun init() {
        llmPrompt = loadContent(llmPrompt)
    }

    private fun loadContent(path: String): String {
        val resource = resourceLoader.getResource(path)
        return resource.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
    }
}