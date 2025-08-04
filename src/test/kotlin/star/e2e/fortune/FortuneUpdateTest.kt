package star.e2e.fortune

import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import star.home.fortune.repository.FortuneRepository
import star.home.fortune.service.FortuneUpdateService
import kotlin.test.Test


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FortuneUpdateTest {

    @LocalServerPort
    private lateinit var port: Integer

    @Autowired
    private lateinit var updateService: FortuneUpdateService

    @Autowired
    private lateinit var repository: FortuneRepository

    @Test
    fun fortuneUpdateTest() {
        runBlocking {
            updateService.updateFortuneWithDelay()
            val fortunes = repository.findAll()

            fortunes.forEach {
                println(it.content)
            }
        }


    }

}