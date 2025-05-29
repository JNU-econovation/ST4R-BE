package star.team.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import star.common.dto.response.internal.Author;
import star.home.board.model.vo.Jido;

@Builder
public record TeamDetailsResponse(
        Long id,
        Author author,
        Boolean isViewerAuthor,
        List<String> imageUrls,
        String name,
        String description,
        Jido location,
        LocalDateTime whenToMeet,
        Integer nowParticipants,
        Integer maxParticipants,
        LocalDateTime createdAt,
        Integer likeCount,
        Boolean liked,
        Boolean isPublic,
        Boolean isJoinable
) { }
