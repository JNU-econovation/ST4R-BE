package star.home.comment.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import star.home.comment.model.entity.Comment;
import star.member.dto.MemberInfoDTO;

@Builder
@Getter
public class CommentDTO {

    private static final String SOFT_DELETED_COMMENT_CONTENT = "삭제된 댓글 입니다.";

    private Long id;
    private MemberInfoDTO memberInfoDTO;
    private Long boardId;
    private List<CommentDTO> childCommentDTOs;
    private Integer depth;
    private String content;
    private LocalDateTime createdAt;

    public static CommentDTO from(Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentDTO.builder()
                .id(comment.getId())
                .memberInfoDTO(MemberInfoDTO.from(comment.getAuthor()))
                .boardId(comment.getBoard().getId())
                .depth(comment.getDepth())
                .content(comment.isDeprecated() ?
                        SOFT_DELETED_COMMENT_CONTENT : comment.getContent().getValue())
                .createdAt(comment.getCreatedAt())
                .childCommentDTOs(new ArrayList<>())
                .build();
    }

    public void addChildCommentDTO(CommentDTO commentDTO) {
        this.childCommentDTOs.add(commentDTO);
    }

}
