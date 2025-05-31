package star.home.dto;

import java.time.OffsetDateTime;
import lombok.Builder;
import star.common.util.CommonTimeUtils;
import star.home.board.model.entity.Board;
import star.common.model.vo.Jido;
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
        OffsetDateTime createdAt
) {

    private static final Integer CONTENT_PREVIEW_MAX_LENGTH = 100;

    public static BoardPeekDTO from(Board board, String imageUrl, Boolean liked) {
        String contentText = board.getContent().text();
        Jido map = board.getContent().map();
        Marker marker = (map == null) ? null : map.marker();

        return BoardPeekDTO.builder()
                .id(board.getId())
                .imageUrl(imageUrl)
                .title(board.getTitle().value())
                .contentPreview(
                        contentText.length() > CONTENT_PREVIEW_MAX_LENGTH
                                ? contentText.substring(0, CONTENT_PREVIEW_MAX_LENGTH)
                                : contentText)
                .marker(marker)
                .category(board.getCategory().getName().name())
                .viewCount(board.getViewCount())
                .commentCount(board.getCommentCount())
                .likeCount(board.getHeartCount())
                .liked(liked)
                .createdAt(CommonTimeUtils.convertLocalDateTimeToOffsetDateTime(board.getCreatedAt()))
                .build();
    }

}
