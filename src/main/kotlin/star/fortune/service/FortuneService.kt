package star.fortune.service

import org.springframework.stereotype.Service
import star.fortune.dto.response.FortuneResponse
import star.fortune.exception.FortuneUpdatingException
import star.fortune.model.entity.Fortune
import star.fortune.repository.FortuneRepository
import star.member.constants.Constellation
import star.member.dto.MemberInfoDTO
import star.member.service.MemberService

@Service
class FortuneService(
    private val memberService: MemberService,
    private val repository: FortuneRepository
) {

    fun getTodayFortune(memberInfo: MemberInfoDTO): FortuneResponse {
        val member = memberService.getMemberEntityById(memberInfo.id)
        val constellation = Constellation.fromDate(member.birthDate.value)
        val fortune: Fortune = repository
            .findByConstellationAndDate(constellation, member.birthDate.value)
            ?: throw FortuneUpdatingException()

        return FortuneResponse(
            date = fortune.date,
            constellation = constellation,
            content = fortune.content
        )
    }

}