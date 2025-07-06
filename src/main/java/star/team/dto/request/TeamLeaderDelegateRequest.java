package star.team.dto.request;

import lombok.Builder;

@Builder
public record TeamLeaderDelegateRequest(
        Long targetMemberId
) { }
