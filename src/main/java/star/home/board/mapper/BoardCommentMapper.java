package star.home.board.mapper;

import java.util.List;
import star.common.dto.response.internal.Author;
import star.home.comment.dto.CommentDTO;
import star.home.comment.dto.response.CommentResponse;

public class BoardCommentMapper {

    public static List<CommentResponse> toCommentVOs(List<CommentDTO> commentDTOs, Long viewerId,
            Long authorId) {
        return commentDTOs.stream()
                .map(dto -> toCommentResponse(dto, viewerId, authorId))
                .toList();
    }

    private static CommentResponse toCommentResponse(CommentDTO dto, Long viewerId, Long authorId) {
        return CommentResponse.builder()
                .id(dto.getId())
                .author(Author.builder()
                        .id(dto.getMemberInfoDTO().id())
                        .imageUrl(dto.getMemberInfoDTO().profileImageUrl())
                        .nickname(dto.getMemberInfoDTO().email().value())
                        .build())
                .isViewerAuthor(viewerId.equals(dto.getMemberInfoDTO().id()))
                .isCommenterAuthor(authorId.equals(dto.getMemberInfoDTO().id()))
                .content(dto.getContent())
                .createdAt(dto.getCreatedAt())
                .depth(dto.getDepth())
                .childComments(toCommentVOs(dto.getChildCommentDTOs(), viewerId, authorId))
                .build();
    }
}