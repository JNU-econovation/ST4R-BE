package star.team.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import star.common.dto.internal.Author;
import star.common.model.vo.Jido;

@Builder
public record TeamDetailsResponse(
        Long id,
        Author author,
        Boolean isViewerAuthor,
        List<String> imageUrls,
        String name,
        String description,
        Jido location,
        OffsetDateTime whenToMeet,
        Integer nowParticipants,
        Integer maxParticipants,
        OffsetDateTime createdAt,
        Integer likeCount,
        Boolean liked,
        Boolean isPublic,
        Boolean isJoinable
) { }
