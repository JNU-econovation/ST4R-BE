package star.home.board.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
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
        LocalDateTime createdAt,
        Integer likeCount,
        Integer commentCount
) {
    @Builder
    public record Author (
            Long id,
            String imageUrl,
            String nickname
    ) { }
}
