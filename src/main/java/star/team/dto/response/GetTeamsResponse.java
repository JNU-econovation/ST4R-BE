package star.team.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import star.common.model.vo.Jido;
import star.common.util.CommonTimeUtils;
import star.team.model.entity.Team;

@Builder
public record GetTeamsResponse(
        Long id,
        List<String> imageUrls,
        String name,
        OffsetDateTime whenToMeet,
        Jido location,
        Integer currentParticipantCount,
        Integer maxParticipantCount,
        Boolean liked,
        Boolean isPublic,
        Boolean joinable,
        Boolean banned,
        Boolean isFull,
        Boolean joined
) {

    public static GetTeamsResponse from(
            Team team,
            List<String> imageUrls,
            Boolean liked,
            Boolean isPublic,
            Boolean joined,
            Boolean banned,
            Boolean isFull,
            Boolean joinable
    ) {
        return GetTeamsResponse.builder()
                .id(team.getId())
                .imageUrls(imageUrls)
                .name(team.getName().getValue())
                .whenToMeet(
                        CommonTimeUtils.convertLocalDateTimeToOffsetDateTime(team.getWhenToMeet())
                )
                .location(team.getLocation())
                .currentParticipantCount(team.getParticipant().getCurrent())
                .maxParticipantCount(team.getParticipant().getCapacity())
                .liked(liked)
                .isFull(isFull)
                .joinable(joinable)
                .joined(joined)
                .banned(banned)
                .isPublic(isPublic)
                .build();
    }
}
