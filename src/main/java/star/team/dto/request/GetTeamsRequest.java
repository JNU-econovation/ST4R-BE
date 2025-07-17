package star.team.dto.request;

import static star.home.constants.LobbySearchConstants.SEARCH_PARAM_MAX_LENGTH;
import static star.home.constants.LobbySearchConstants.SEARCH_PARAM_MIN_LENGTH;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import org.springframework.lang.Nullable;
import star.common.dto.LocationParamsDTO;
import star.common.model.vo.CircularArea;
import star.team.annotation.ValidOffsetDateTimeRange;

@ValidOffsetDateTimeRange
public record GetTeamsRequest(

        @Size(min = SEARCH_PARAM_MIN_LENGTH, max = SEARCH_PARAM_MAX_LENGTH, message = "검색어는 {min}자 이상 {max}자 이하여야 합니다.")
        @Pattern(regexp = ".*\\S.*", message = "검색어는 공백만으로 구성될 수 없습니다.")
        @Nullable String name,

        @Nullable @Future(message = "모임 시간은 미래여야 합니다")
        OffsetDateTime meetBetweenStart,

        @Nullable @Future(message = "모임 시간은 미래여야 합니다")
        OffsetDateTime meetBetweenEnd,

        @Size(min = SEARCH_PARAM_MIN_LENGTH, max = SEARCH_PARAM_MAX_LENGTH, message = "검색어는 {min}자 이상 {max}자 이하여야 합니다.")
        @Pattern(regexp = ".*\\S.*", message = "검색어는 공백만으로 구성될 수 없습니다.")
        @Nullable String leaderName,

        @Nullable LocationParamsDTO location,
        @Nullable Boolean includePast
) {
    public CircularArea circularArea() {
        return location != null ? location.toCircularArea() : null;
    }
}
