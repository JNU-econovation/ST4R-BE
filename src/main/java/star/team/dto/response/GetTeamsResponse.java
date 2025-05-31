package star.team.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import star.common.model.vo.Jido;

@Builder
public record GetTeamsResponse(
        Long id,
        List<String> imageUrls,
        String name,
        OffsetDateTime whenToMeet,
        Jido location,
        Integer currentParticipantCount,
        Integer maxParticipantCount,
        Boolean liked,
        Boolean joinable,
        Boolean isPublic
) {

}
