package star.team.chat.dto.response;

import lombok.Builder;

@Builder
public record UnreadChatCountsResponse(
        Long teamId,
        Long unreadCount
) {
}