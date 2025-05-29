package star.home.comment.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import star.common.dto.response.internal.Author;

@Builder
public record CommentResponse (
        Long id,
        Author author,
        Boolean isViewerAuthor,
        Boolean isCommenterAuthor,
        String content,
        LocalDateTime createdAt,
        List<CommentResponse> childComments,
        Integer depth
) { }