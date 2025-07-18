package star.myPage.dto.response;

import java.time.LocalDate;
import lombok.Builder;
import star.member.constants.Constellation;
import star.member.model.vo.Gender;

@Builder
public record MyPageResponse(
        String nickname,
        String email,
        String profileImageUrl,
        LocalDate birthDate,
        Gender gender,
        Constellation constellation
) {

}
