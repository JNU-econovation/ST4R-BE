package star.team.dto;

import lombok.Builder;
import org.springframework.lang.Nullable;
import star.common.dto.LocalDateTimesDTO;
import star.common.model.vo.CircularArea;
import star.team.model.vo.Name;

@Builder
public record TeamSearchDTO(
        @Nullable CircularArea circularArea,
        @Nullable Name name,
        @Nullable String leaderName,
        @Nullable LocalDateTimesDTO meetBetween,
        Boolean includePast
) {

}
