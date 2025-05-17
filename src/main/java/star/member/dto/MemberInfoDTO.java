package star.member.dto;

import lombok.Builder;
import star.member.model.entity.Member;
import star.member.model.entity.Role;
import star.member.model.vo.Email;

@Builder
public record MemberInfoDTO(Long id, Email email, Role role, String profileImageUrl) {

    public static MemberInfoDTO from(Member member) {
        return MemberInfoDTO.builder()
                .id(member.getId())
                .email(member.getEmail())
                .role(member.getRole())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }

}
