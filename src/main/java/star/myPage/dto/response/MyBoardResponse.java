package star.myPage.dto.response;

import static star.home.board.constants.BoardConstants.CONTENT_PREVIEW_MAX_LENGTH;

import java.time.OffsetDateTime;
import lombok.Builder;
import star.common.util.CommonTimeUtils;
import star.home.board.model.entity.Board;
import star.home.dto.BoardPeekDTO;

@Builder
public record MyBoardResponse(
        Long id,
        Boolean liked,
        String title,
        String thumbnailImageUrl,
        String contentPreview,
        String category,
        Integer viewCount,
        OffsetDateTime createdAt,
        Integer likeCount,
        Integer commentCount
) {
    public static MyBoardResponse from(BoardPeekDTO boardPeekDTO) {
        return MyBoardResponse.builder()
                .id(boardPeekDTO.id())
                .liked(boardPeekDTO.liked())
                .title(boardPeekDTO.title())
                .thumbnailImageUrl(boardPeekDTO.imageUrl())
                .contentPreview(boardPeekDTO.contentPreview())
                .category(boardPeekDTO.category())
                .viewCount(boardPeekDTO.viewCount())
                .createdAt(boardPeekDTO.createdAt())
                .likeCount(boardPeekDTO.likeCount())
                .commentCount(boardPeekDTO.commentCount())
                .build();
    }

    public static MyBoardResponse fromForLikedBoard(Board board, String thumbnailImageUrl) {
        String contentText = board.getContent().getText();

        return MyBoardResponse.builder()
                .id(board.getId())
                .liked(true)
                .title(board.getTitle().value())
                .thumbnailImageUrl(thumbnailImageUrl)
                .contentPreview(
                        contentText.length() > CONTENT_PREVIEW_MAX_LENGTH
                                ? contentText.substring(0, CONTENT_PREVIEW_MAX_LENGTH)
                                : contentText)
                .category(board.getCategory().getName().toString())
                .viewCount(board.getViewCount())
                .createdAt(
                        CommonTimeUtils.convertLocalDateTimeToOffsetDateTime(board.getCreatedAt())
                )
                .likeCount(board.getHeartCount())
                .commentCount(board.getCommentCount())
                .build();
    }
}
