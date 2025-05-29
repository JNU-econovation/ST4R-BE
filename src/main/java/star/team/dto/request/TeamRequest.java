package star.team.dto.request;

import static star.common.constants.CommonConstants.MAX_IMAGE_COUNT;
import static star.team.constants.TeamConstants.*;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import star.home.board.model.vo.Jido;

public record TeamRequest(

        @NotNull(message = "이미지 url 필드를 입력해주세요")
        @Size(max = MAX_IMAGE_COUNT, message = "이미지는 최대 {max}개까지 등록할 수 있습니다.")
        List<String> imageUrls,

        @NotBlank(message = "제목을 입력해주세요")
        @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH, message = "제목의 길이는 최소 {min}자, 최대 {max}자여야 합니다.")
        String name,

        LocalDateTime whenToMeet,

        @Min(PARTICIPANT_MIN_CAPACITY) @Max(PARTICIPANT_MAX_CAPACITY)
        Integer maxParticipantCount,

        @Nullable
        String password,

        Jido location,

        @Nullable
        String description
) {

}
