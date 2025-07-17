package star.team.dto;

import lombok.Builder;
import org.springframework.lang.Nullable;
import star.common.dto.LocalDateTimesDTO;
import star.common.model.vo.CircularArea;

@Builder
public record TeamSearchDTO(
        @Nullable CircularArea circularArea,
        @Nullable String name,
        @Nullable String leaderName,
        @Nullable LocalDateTimesDTO meetBetween,
        Boolean includePast
) {

}
