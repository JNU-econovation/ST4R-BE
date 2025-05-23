package star.home.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import star.home.board.model.entity.Board;
import star.home.board.model.vo.Marker;

@Builder
public record BoardPeekDTO(
        Long id,
        String imageUrl,
        String title,
        String contentPreview,
        Marker marker,
        String category,
        Integer viewCount,
        Integer commentCount,
        Integer likeCount,
        Boolean liked,
        LocalDateTime createdAt
) {

    private static final Integer CONTENT_PREVIEW_MAX_LENGTH = 100;

    public static BoardPeekDTO from(Board board, String imageUrl, Boolean liked) {
        String contentText = board.getContent().text();

        return BoardPeekDTO.builder()
                .id(board.getId())
                .imageUrl(imageUrl)
                .title(board.getTitle().value())
                .contentPreview(
                        contentText.length() > CONTENT_PREVIEW_MAX_LENGTH
                                ? contentText.substring(0, CONTENT_PREVIEW_MAX_LENGTH)
                                : contentText)
                .marker(board.getContent().map().marker())
                .category(board.getCategory().getName())
                .viewCount(board.getViewCount())
                .commentCount(board.getCommentCount())
                .likeCount(board.getHeartCount())
                .liked(liked)
                .createdAt(board.getCreatedAt())
                .build();
    }

}
