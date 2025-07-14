package star.member.dto.reqeust;

import static star.constants.MemberConstants.NICKNAME_MAX_LENGTH;
import static star.constants.MemberConstants.NICKNAME_MIN_LENGTH;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;
import star.member.model.vo.Gender;

@Builder
public record CompleteRegistrationRequest(
        @Past(message = "생년월일은 미래일 수 없습니다.")
        LocalDate birthDate,

        Gender gender,

        @NotBlank(message = "닉네임을 입력해주세요")
        @Size(min = NICKNAME_MIN_LENGTH, max = NICKNAME_MAX_LENGTH, message = "닉네임의 길이는 최소 {min}자, 최대 {max}자여야 합니다.")
        String nickname,

        @Nullable
        String profileImageUrl
) {

}