package star.team.dto.request;

import static star.common.constants.CommonConstants.MAX_IMAGE_COUNT;
import static star.team.constants.TeamConstants.*;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import star.common.model.vo.Jido;

public record TeamRequest(

        @NotNull(message = "이미지 url 필드를 입력해주세요")
        @Size(max = MAX_IMAGE_COUNT, message = "이미지는 최대 {max}개까지 등록할 수 있습니다.")
        List<String> imageUrls,

        @NotBlank(message = "모임 이름을 입력해주세요")
        @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH, message = "모임 이름의 길이는 최소 {min}자, 최대 {max}자여야 합니다.")
        String name,

        @NotNull(message = "모임 시간을 입력해주세요")
        @Future(message = "모임 시간은 미래여야 합니다")
        // ux적인 우려사항 : 모임 시간이 지남 -> 사용자가 모임 수정하려다가 취소할 때
        OffsetDateTime whenToMeet,

        @Min(PARTICIPANT_MIN_CAPACITY) @Max(PARTICIPANT_MAX_CAPACITY)
        Integer maxParticipantCount,

        @Nullable
        String password,

        @NotNull(message = "위치 정보를 입력해주세요")
        Jido location,

        @Nullable
        String description
) {

}
