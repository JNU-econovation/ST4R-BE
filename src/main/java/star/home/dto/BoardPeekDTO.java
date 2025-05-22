package star.home.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import star.home.board.dto.response.BoardResponse;
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
        Integer likeCount,
        Boolean liked,
        LocalDateTime createdAt
) {

    private static final Integer CONTENT_PREVIEW_MAX_LENGTH = 100;

    public static BoardPeekDTO from(BoardResponse boardResponse) {
        String contentText = boardResponse.content().text();
        Marker marker = boardResponse.content().map().marker();

        return BoardPeekDTO.builder()
                .id(boardResponse.id())
                .imageUrl(boardResponse.imageUrls().getFirst())
                .liked(boardResponse.liked())
                .title(boardResponse.title())
                .contentPreview(
                        contentText.length() > CONTENT_PREVIEW_MAX_LENGTH ? contentText.substring(0,
                                CONTENT_PREVIEW_MAX_LENGTH) : contentText)
                .marker(marker)
                .category(boardResponse.category())
                .viewCount(boardResponse.viewCount())
                .likeCount(boardResponse.likeCount())
                .createdAt(boardResponse.createdAt())
                .build();

    }
}
