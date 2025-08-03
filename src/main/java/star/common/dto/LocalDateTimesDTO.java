package star.common.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LocalDateTimesDTO {

    private final LocalDateTime start;
    private final LocalDateTime end;

    @Builder
    public LocalDateTimesDTO(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

}
