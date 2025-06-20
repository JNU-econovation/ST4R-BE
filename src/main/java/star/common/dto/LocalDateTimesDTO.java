package star.common.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record LocalDateTimesDTO(
        LocalDateTime start,
        LocalDateTime end
) {

}
