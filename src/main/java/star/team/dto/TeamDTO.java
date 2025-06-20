package star.team.dto;

import jakarta.annotation.Nullable;
import java.time.OffsetDateTime;
import lombok.Builder;
import star.common.model.vo.Jido;
import star.team.model.vo.Description;
import star.team.model.vo.Name;
import star.team.model.vo.PlainPassword;

@Builder
public record TeamDTO(
        Name name,
        OffsetDateTime whenToMeet,
        Integer maxParticipantCount,
        PlainPassword plainPassword,
        Jido location,

        @Nullable
        Description description
) {

}
