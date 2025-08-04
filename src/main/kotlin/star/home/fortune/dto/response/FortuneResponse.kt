package star.home.fortune.dto.response

import star.member.constants.Constellation
import java.time.LocalDate

data class FortuneResponse(
    val date: LocalDate,
    val constellation: Constellation,
    val content: String
)
