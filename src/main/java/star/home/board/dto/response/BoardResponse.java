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
        String imageUrl,
        Content content,
        String category,
        Integer viewCount,
        LocalDateTime createdAt,
        Integer likeCount,
        Integer commentCount,
        List<Comment> comments
) {
    @Builder
    public record Author (
            Long id,
            String imageUrl,
            String nickname
    ) {

    }

    @Builder
    public record Comment (
            Long id,
            Author author,
            Boolean isViewerAuthor,
            Boolean isCommenterAuthor,
            String content,
            LocalDateTime createdAt
    ) { }
}
