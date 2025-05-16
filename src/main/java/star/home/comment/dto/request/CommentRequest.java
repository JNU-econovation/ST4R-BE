package star.home.comment.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record CommentRequest(
        @NotBlank(message = "댓글이 공백일 수 없습니다.")
        @Size(min = 2, max = 300, message = "댓글의 길이는 최소 {min}자, 최대 {max}자여야 합니다.")
        String content,

        @Nullable
        Long parentCommentId
) {

}
