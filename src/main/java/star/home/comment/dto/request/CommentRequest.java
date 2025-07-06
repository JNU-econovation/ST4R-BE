package star.home.comment.dto.request;

import static star.home.comment.constants.CommentConstants.COMMENT_CONTENT_MAX_LENGTH;
import static star.home.comment.constants.CommentConstants.COMMENT_CONTENT_MIN_LENGTH;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CommentRequest(
        @NotBlank(message = "댓글이 공백일 수 없습니다.")
        @Size(min = COMMENT_CONTENT_MIN_LENGTH, max = COMMENT_CONTENT_MAX_LENGTH, message = "댓글의 길이는 최소 {min}자, 최대 {max}자여야 합니다.")
        String content,

        @Nullable
        Long parentCommentId
) {

}
