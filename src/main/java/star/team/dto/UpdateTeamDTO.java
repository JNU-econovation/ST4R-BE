package star.team.dto;

import jakarta.annotation.Nullable;
import java.time.OffsetDateTime;
import lombok.Builder;
import star.common.model.vo.Jido;
import star.team.model.vo.Description;
import star.team.model.vo.Name;
import star.team.model.vo.PlainPassword;

@Builder
public record UpdateTeamDTO(
        Name name,
        Integer maxParticipantCount,
        Jido location,

        @Nullable
        OffsetDateTime newWhenToMeet,

        @Nullable
        PlainPassword plainPassword,

        @Nullable
        Description description
        ) {

}
