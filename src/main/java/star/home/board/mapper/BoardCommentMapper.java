package star.home.board.mapper;

import java.util.List;
import star.home.board.dto.response.BoardResponse.Author;
import star.home.board.dto.response.BoardResponse.Comment;
import star.home.comment.dto.CommentDTO;

public class BoardCommentMapper {

    public static List<Comment> toCommentVOs(List<CommentDTO> commentDTOs, Long viewerId, Long authorId) {
        return commentDTOs.stream()
                .map(dto -> toCommentVO(dto, viewerId, authorId))
                .toList();
    }

    private static Comment toCommentVO(CommentDTO dto, Long viewerId, Long authorId) {
        return Comment.builder()
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