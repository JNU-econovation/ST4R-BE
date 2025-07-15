package star.myPage.dto.response;

import java.time.LocalDate;
import lombok.Builder;
import star.member.model.vo.Gender;

@Builder
public record MyPageResponse(
        String nickname,
        String email,
        LocalDate birthDate,
        Gender gender,
        String constellation
) {

}
