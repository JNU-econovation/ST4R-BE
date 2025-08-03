package star.myPage.dto.request

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import star.common.exception.client.BadDataSyntaxException
import star.common.infra.aws.annotation.S3ImageUrl
import star.constants.MemberConstants.NICKNAME_MAX_LENGTH
import star.constants.MemberConstants.NICKNAME_MIN_LENGTH


data class UpdateProfileRequest(
    @field:S3ImageUrl
    val profileImageUrlToChange: String?,

    @param:Size(
        min = NICKNAME_MIN_LENGTH,
        max = NICKNAME_MAX_LENGTH,
        message = "닉네임의 길이는 최소 {min}자, 최대 {max}자여야 합니다."
    )
    val nicknameToChange: String?,

    @param:NotNull(message = "changeProfileImage를 입력해주세요")
    val changeProfileImage: Boolean,

    @param:NotNull(message = "changeNickname 입력해주세요")
    val changeNickname: Boolean
) {

    init {
        validate()
    }

    fun validate() {
        if (changeProfileImage && profileImageUrlToChange == null) {
            throw BadDataSyntaxException(
                "changeProfileImage가 true이면 profileImageUrlToChange를 입력해주세요."
            )
        }

        if (changeNickname && nicknameToChange == null) {
            throw BadDataSyntaxException("changeNickname이 true이면 nicknameToChange를 입력해주세요.")
        }

        if (!changeNickname && !changeProfileImage) {
            throw BadDataSyntaxException(
                "changeNickname과 changeProfileImage 둘 다 false일 수 없습니다."
            )
        }
    }

}
