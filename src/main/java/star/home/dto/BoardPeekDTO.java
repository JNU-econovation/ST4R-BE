package star.home.dto;

import static star.home.board.constants.BoardConstants.CONTENT_PREVIEW_MAX_LENGTH;

import java.time.OffsetDateTime;
import lombok.Builder;
import star.common.model.vo.Jido;
import star.common.model.vo.Marker;
import star.common.util.CommonTimeUtils;
import star.home.board.model.entity.Board;

@Builder
public record BoardPeekDTO(
        Long id,
        Long authorId,
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

    public static BoardPeekDTO from(Board board, String imageUrl, Boolean liked) {
        String contentText = board.getContent().getText();
        Jido map = board.getContent().getMap();
        Marker marker = (map == null) ? null : map.getMarker();

        return BoardPeekDTO.builder()
                .id(board.getId())
                .authorId(board.getMember().getId())
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
