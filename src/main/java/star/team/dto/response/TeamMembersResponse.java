package star.team.dto.response;

import lombok.Builder;
import star.member.dto.MemberInfoDTO;

@Builder
public record TeamMembersResponse(
        Long id,
        String imageUrl,
        String nickname,
        Boolean isLeader,
        Boolean isMe
) {
    public static TeamMembersResponse from(MemberInfoDTO memberInfoDTO, Boolean isLeader, Boolean isMe) {
        return TeamMembersResponse.builder()
                .id(memberInfoDTO.id())
                .nickname(memberInfoDTO.nickname().value())
                .imageUrl(memberInfoDTO.profileImageUrl())
                .isLeader(isLeader)
                .isMe(isMe)
                .build();
    }
}
