package star.team.chat.dto.response;

import lombok.Builder;

@Builder
public record ChatPreviewResponse(
        Long teamId,
        Long unreadCount,
        String recentMessage
) {
}