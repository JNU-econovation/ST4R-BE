package star.home.fortune.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import star.home.fortune.dto.response.FortuneResponse
import star.home.fortune.exception.FortuneUpdatingException
import star.home.fortune.model.entity.Fortune
import star.home.fortune.repository.FortuneRepository
import star.member.constants.Constellation
import star.member.dto.MemberInfoDTO
import star.member.service.MemberService
import java.time.LocalDate
import java.time.ZoneId

@Service
class FortuneService(
    private val memberService: MemberService,
    private val repository: FortuneRepository
) {

    @Transactional(readOnly = true)
    fun getTodayFortune(memberInfo: MemberInfoDTO): FortuneResponse {
        val member = memberService.getMemberEntityById(memberInfo.id)
        val constellation = Constellation.fromDate(member.birthDate.value)
        val fortune: Fortune = repository
            .findByConstellationAndDate(constellation, LocalDate.now(ZoneId.of("Asia/Seoul")))
            ?: throw FortuneUpdatingException()

        return FortuneResponse(
            date = fortune.date,
            constellation = constellation,
            content = fortune.content.replace("%s", member.nickname.value)
        )
    }

}