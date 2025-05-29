package star.home.board.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import star.common.dto.response.internal.Author;
import star.home.board.model.vo.Content;

@Builder
public record BoardResponse (
        Long id,
        Author author,
        Boolean isViewerAuthor,
        Boolean liked,
        String title,
        List<String> imageUrls,
        Content content,
        String category,
        Integer viewCount,
        OffsetDateTime createdAt,
        Integer likeCount,
        Integer commentCount
) { }
