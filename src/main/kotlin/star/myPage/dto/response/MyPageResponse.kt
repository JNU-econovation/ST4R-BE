package star.myPage.dto.response

import star.member.constants.Constellation
import star.member.model.vo.Gender
import java.time.LocalDate

data class MyPageResponse(
    val nickname: String,
    val email: String,
    val profileImageUrl: String,
    val birthDate: LocalDate,
    val gender: Gender,
    val constellation: Constellation
)