package star.team.dto.request;

import jakarta.validation.constraints.Future;
import java.time.OffsetDateTime;
import org.springframework.lang.Nullable;
import star.common.dto.LocationParamsDTO;
import star.common.model.vo.CircularArea;
import star.team.annotation.ValidZonedDateTimeRange;

@ValidZonedDateTimeRange
public record GetTeamsRequest(
        @Nullable String name,
        @Nullable @Future(message = "모임 시간은 미래여야 합니다")
        OffsetDateTime meetBetweenStart,
        @Nullable @Future(message = "모임 시간은 미래여야 합니다")
        OffsetDateTime meetBetweenEnd,
        @Nullable String leaderName,
        @Nullable LocationParamsDTO location,
        @Nullable Boolean includePast
) {
    public CircularArea circularArea() {
        return location != null ? location.toCircularArea() : null;
    }
}
