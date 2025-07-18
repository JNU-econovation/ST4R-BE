package star.home.board.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import star.common.dto.internal.Author;
import star.common.util.CommonTimeUtils;
import star.home.board.model.entity.Board;
import star.home.board.model.vo.Content;
import star.member.dto.MemberInfoDTO;

@Builder
public record BoardResponse (
        Long id,
        Author author,
        Boolean isViewerAuthor,
        Boolean liked,
        String title,
        List<String> imageUrls,
        Content content,
        String category,
        Integer viewCount,
        OffsetDateTime createdAt,
        Integer likeCount,
        Integer commentCount
) {

    public static BoardResponse from(Board board, boolean isViewerAuthor, boolean hasHearted, List<String> imageUrls) {
        Author author = MemberInfoDTO.from(board.getMember()).toAuthor();

        return BoardResponse.builder()
                .id(board.getId())
                .author(author)
                .isViewerAuthor(isViewerAuthor)
                .liked(hasHearted)
                .title(board.getTitle().value())
                .imageUrls(imageUrls)
                .content(board.getContent())
                .category(board.getCategory().getName().name())
                .viewCount(board.getViewCount())
                .createdAt(
                        CommonTimeUtils.convertLocalDateTimeToOffsetDateTime(board.getCreatedAt()))
                .likeCount(board.getHeartCount())
                .commentCount(board.getCommentCount())
                .build();
    }

}
