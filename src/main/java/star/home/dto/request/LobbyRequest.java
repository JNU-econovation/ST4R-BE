package star.home.dto.request;


import static star.home.constants.LobbySearchConstants.SEARCH_PARAM_MAX_LENGTH;
import static star.home.constants.LobbySearchConstants.SEARCH_PARAM_MIN_LENGTH;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;
import star.common.dto.LocationParamsDTO;
import star.common.model.vo.CircularArea;
import star.home.category.model.vo.CategoryName;
import star.home.constants.Period;

@Builder
public record LobbyRequest(
        @Nullable Period period,
        List<CategoryName> categories,

        @Size(min = SEARCH_PARAM_MIN_LENGTH, max = SEARCH_PARAM_MAX_LENGTH, message = "검색어는 {min}자 이상 {max}자 이하여야 합니다.")
        @Pattern(regexp = ".*\\S.*", message = "검색어는 공백만으로 구성될 수 없습니다.")
        @Nullable String title,

        @Size(min = SEARCH_PARAM_MIN_LENGTH, max = SEARCH_PARAM_MAX_LENGTH, message = "검색어는 {min}자 이상 {max}자 이하여야 합니다.")
        @Pattern(regexp = ".*\\S.*", message = "검색어는 공백만으로 구성될 수 없습니다.")
        @Nullable String content,

        @Size(min = SEARCH_PARAM_MIN_LENGTH, max = SEARCH_PARAM_MAX_LENGTH, message = "검색어는 {min}자 이상 {max}자 이하여야 합니다.")
        @Pattern(regexp = ".*\\S.*", message = "검색어는 공백만으로 구성될 수 없습니다.")
        @Nullable String authorName,

        @Nullable CategoryName categoryName,
        @Nullable LocationParamsDTO location
) {

    public CircularArea circularArea() {
        return location != null ? location.toCircularArea() : null;
    }
}