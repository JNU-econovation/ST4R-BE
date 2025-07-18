package star.team.dto.request;

import static star.common.constants.CommonConstants.MAX_IMAGE_COUNT;
import static star.team.constants.TeamConstants.NAME_MAX_LENGTH;
import static star.team.constants.TeamConstants.NAME_MIN_LENGTH;
import static star.team.constants.TeamConstants.PARTICIPANT_MAX_CAPACITY;
import static star.team.constants.TeamConstants.PARTICIPANT_MIN_CAPACITY;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import star.common.infra.aws.annotation.S3ImageUrl;
import star.common.model.vo.Jido;

@Builder
public record CreateTeamRequest(

        @NotNull(message = "이미지 url 필드를 입력해주세요")
        @Size(max = MAX_IMAGE_COUNT, message = "이미지는 최대 {max}개까지 등록할 수 있습니다.")
        @S3ImageUrl
        List<String> imageUrls,

        @NotBlank(message = "모임 이름을 입력해주세요")
        @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH, message = "모임 이름의 길이는 최소 {min}자, 최대 {max}자여야 합니다.")
        String name,

        @NotNull(message = "모임 시간을 입력해주세요")
        @Future(message = "모임 시간은 미래여야 합니다")
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
