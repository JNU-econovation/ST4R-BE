package star.member.dto;

import star.member.model.vo.Email;

public record SocialRegisterDTO(
        Email email,
        String profileImageUrl,
        String SocialAccessToken
) {

}
