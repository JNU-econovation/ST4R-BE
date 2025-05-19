package star.team.service.internal;

import static star.common.constants.CommonConstants.MAX_IMAGE_COUNT;

import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.common.exception.client.TooManyImageUrlsException;
import star.team.model.entity.Team;
import star.team.model.entity.TeamImage;
import star.team.repository.TeamImageRepository;

@Service
@RequiredArgsConstructor
public class TeamImageDataService {

    private final TeamImageRepository teamImageRepository;

    @Transactional
    public void addImageUrls(Team team, List<String> imageUrls) {
        overwriteImageUrls(team, imageUrls);
    }

    @Transactional
    public void overwriteImageUrls(Team team, List<String> imageUrls) {
        if (imageUrls.size() > MAX_IMAGE_COUNT) {
            throw new TooManyImageUrlsException(MAX_IMAGE_COUNT);
        }

        teamImageRepository.deleteTeamImageByTeamId(team.getId());

        List<TeamImage> newTeamImages = IntStream.range(0, imageUrls.size())
                .mapToObj(i -> TeamImage.builder()
                        .team(team)
                        .imageUrl(imageUrls.get(i))
                        .sortOrder(i)
                        .build())
                .toList();

        teamImageRepository.saveAll(newTeamImages);
    }

    @Transactional
    public void deleteBoardImageUrls(Long teamId) {
        teamImageRepository.deleteTeamImagesByTeamId(teamId);
    }
}
