package star.team.dto.response;

import lombok.Builder;
import star.common.model.vo.Jido;
import star.team.model.entity.Team;

@Builder
public record GetMyTeamsResponse(
        Long id,
        String title,
        Integer nowParticipants,
        Integer maxParticipants,
        String thumbnailImageUrl,
        Jido location
) {

    public static GetMyTeamsResponse from(Team team, String thumbnailImageUrl) {
        return GetMyTeamsResponse.builder()
                .id(team.getId())
                .title(team.getName().getValue())
                .nowParticipants(team.getParticipant().getCurrent())
                .maxParticipants(team.getParticipant().getCapacity())
                .thumbnailImageUrl(thumbnailImageUrl)
                .location(team.getLocation())
                .build();
    }

}
