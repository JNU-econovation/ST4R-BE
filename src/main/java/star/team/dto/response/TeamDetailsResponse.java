package star.team.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import star.common.dto.internal.Author;
import star.common.model.vo.Jido;
import star.common.util.CommonTimeUtils;
import star.team.model.entity.Team;
import star.team.model.vo.Description;

@Builder
public record TeamDetailsResponse(
        Long id,
        Author author,
        Boolean isViewerAuthor,
        List<String> imageUrls,
        String name,
        String description,
        Jido location,
        OffsetDateTime whenToMeet,
        Integer nowParticipants,
        Integer maxParticipants,
        OffsetDateTime createdAt,
        Integer likeCount,
        Boolean liked,
        Boolean isPublic,
        Boolean joinable,
        Boolean banned,
        Boolean isFull,
        Boolean joined
) {

    public static TeamDetailsResponse from(
            Team team,
            Author author,
            Boolean isViewerAuthor,
            List<String> imageUrls,
            Boolean liked,
            Boolean isPublic,
            Boolean joinable,
            Boolean banned,
            Boolean isFull,
            Boolean joined
    ) {

        Description description = team.getDescription();

        return TeamDetailsResponse.builder()
                .id(team.getId())
                .author(author)
                .isViewerAuthor(isViewerAuthor)
                .imageUrls(imageUrls)
                .name(team.getName().getValue())
                .description(description == null ? null : description.getValue())
                .location(team.getLocation())
                .whenToMeet(
                        CommonTimeUtils.convertLocalDateTimeToOffsetDateTime(team.getWhenToMeet())
                )
                .nowParticipants(team.getParticipant().getCurrent())
                .maxParticipants(team.getParticipant().getCapacity())
                .createdAt(
                        CommonTimeUtils.convertLocalDateTimeToOffsetDateTime(team.getCreatedAt())
                )
                .likeCount(team.getHeartCount())
                .liked(liked)
                .isPublic(isPublic)
                .joinable(joinable)
                .banned(banned)
                .isFull(isFull)
                .joined(joined)
                .build();
    }
}
