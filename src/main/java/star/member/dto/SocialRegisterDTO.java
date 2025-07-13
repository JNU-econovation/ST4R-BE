package star.member.dto;

import lombok.Builder;
import star.member.model.vo.Email;

@Builder
public record SocialRegisterDTO(
        Email email,
        String socialAccessToken
) {

}
