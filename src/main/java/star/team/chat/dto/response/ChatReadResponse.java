package star.team.chat.dto.response;

import java.time.OffsetDateTime;
import lombok.Builder;

@Builder
public record ChatReadResponse(
        Long memberId,
        OffsetDateTime readTime
) { }