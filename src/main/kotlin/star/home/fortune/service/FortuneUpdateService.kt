package star.home.fortune.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import star.home.fortune.client.FortuneClient
import star.home.fortune.model.entity.Fortune
import star.home.fortune.repository.FortuneRepository
import star.member.constants.Constellation
import java.time.LocalDate
import java.time.ZoneId

private const val ONE_SECOND = 1000L
private const val DELAY_TIME = 90 * ONE_SECOND

private val logger = KotlinLogging.logger {}

@Service
class FortuneUpdateService(
    private val client: FortuneClient,
    private val repository: FortuneRepository
) {
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    suspend fun updateFortuneWithDelay() {
        val allConstellations: List<Constellation> = Constellation.entries
        val date = LocalDate.now(ZoneId.of("Asia/Seoul"))
        val chunks = allConstellations.chunked(6)

        chunks.getOrNull(0)?.let { processAndSaveBatch(it, date) }

        logger.info { "별자리 운세 절반 추가 후 ${DELAY_TIME / ONE_SECOND}초 딜레이 중" }

        delay(DELAY_TIME)

        chunks.getOrNull(1)?.let { processAndSaveBatch(it, date) }

        logger.info { "별자리 운세 모두 추가 완료" }
    }

    @Transactional
    suspend fun processAndSaveBatch(constellations: List<Constellation>, date: LocalDate) {
        val fortuneEntities = coroutineScope {
            constellations.map { constellation ->
                async(Dispatchers.IO) {
                    val content = client.getFortune(constellation, date)?.join()?.text() ?: ""
                    Fortune.builder()
                        .constellation(constellation)
                        .content(content)
                        .date(date)
                        .build()
                }
            }.awaitAll()
        }
        repository.saveAll(fortuneEntities)
    }
}