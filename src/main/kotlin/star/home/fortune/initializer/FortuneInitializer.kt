package star.home.fortune.initializer

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import star.home.fortune.repository.FortuneRepository
import star.home.fortune.service.FortuneUpdateService
import java.time.LocalDate

@Component
class FortuneInitializer(
    private val updateService: FortuneUpdateService,
    private val repository: FortuneRepository
) {

    @EventListener(classes = [ApplicationReadyEvent::class])
    suspend fun updateFortuneIfEmpty() {
        if (repository.existsByDate(LocalDate.now()))
            return

        updateService.updateFortuneWithDelay()
    }
}