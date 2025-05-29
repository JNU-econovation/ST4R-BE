package star.team.dto;

import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import lombok.Builder;
import star.home.board.model.vo.Jido;
import star.team.model.vo.Description;
import star.team.model.vo.Name;
import star.team.model.vo.PlainPassword;

@Builder
public record TeamDTO(
        Name name,
        LocalDateTime whenToMeet,
        Integer maxParticipantCount,
        PlainPassword plainPassword,
        Jido location,

        @Nullable
        Description description
) {

}
