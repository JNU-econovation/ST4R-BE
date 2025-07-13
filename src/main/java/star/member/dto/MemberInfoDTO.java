package star.member.dto;

import lombok.Builder;
import star.common.dto.internal.Author;
import star.member.model.entity.Member;
import star.member.model.entity.Role;
import star.member.model.vo.Email;
import star.member.model.vo.Nickname;

@Builder
public record MemberInfoDTO(
        Long id, Email email, Nickname nickname, Role role, String profileImageUrl
) {

    public static MemberInfoDTO from(Member member) {
        return MemberInfoDTO.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }

    public Author toAuthor() {
        return Author.builder()
                .id(this.id())
                .nickname(this.nickname().value())
                .imageUrl(this.profileImageUrl())
                .build();
    }

}
