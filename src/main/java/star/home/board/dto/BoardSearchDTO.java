package star.home.board.dto;

import java.util.List;
import lombok.Builder;
import org.springframework.lang.Nullable;
import star.common.dto.LocalDateTimesDTO;
import star.common.exception.client.BadDataSyntaxException;
import star.common.model.vo.CircularArea;
import star.home.board.model.vo.Content;
import star.home.board.model.vo.Title;
import star.home.category.model.vo.CategoryName;

@Builder
public record BoardSearchDTO(
        @Nullable CircularArea circularArea,
        @Nullable Title title,
        @Nullable Content content,
        @Nullable String authorName,
        /*empty able*/ List<CategoryName> categories,
        LocalDateTimesDTO localDateTimesForSearch
) {

    public BoardSearchDTO {
        validate(localDateTimesForSearch);
    }

    private void validate(LocalDateTimesDTO localDateTimesForSearch) {
        if (localDateTimesForSearch == null) {
            throw new BadDataSyntaxException("검색할 시간을 입력해주세요.");
        }
    }
}
