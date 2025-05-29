package star.team.service.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.common.exception.server.InternalServerException;
import star.common.security.encryption.util.AESEncryptionUtil;
import star.team.dto.request.TeamDTO;
import star.team.exception.TeamNotFoundException;
import star.team.exception.YouAreNotTeamLeaderException;
import star.team.model.entity.Team;
import star.team.model.vo.EncryptedPassword;
import star.team.model.vo.Participant;
import star.team.model.vo.PlainPassword;
import star.team.repository.TeamRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamDataService {
    private final TeamRepository teamRepository;
    private final AESEncryptionUtil aesEncryptionUtil;

    @Transactional
    public Team createTeam(Long memberId, TeamDTO teamDTO) {
        EncryptedPassword encryptedPassword =
                (teamDTO.plainPassword() == null) ? null : encryptPassword(teamDTO.plainPassword());

        Team team = Team.builder()
                .name(teamDTO.name())
                .description(teamDTO.description())
                .encryptedPassword(encryptedPassword)
                .leaderId(memberId)
                .participant(
                        Participant.builder()
                                .current(1).capacity(teamDTO.maxParticipantCount())
                                .build()
                )
                .whenToMeet(teamDTO.whenToMeet())
                .location(teamDTO.location())
                .build();

        return teamRepository.save(team);
    }

    @Transactional(readOnly = true)
    public Team getTeamEntityById(Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(TeamNotFoundException::new);
    }

    @Transactional
    public Team overwriteTeam(Long memberId, Long teamId, TeamDTO teamDTO) {
        EncryptedPassword encryptedPassword =
                (teamDTO.plainPassword() == null) ? null : encryptPassword(teamDTO.plainPassword());

        Team team = getTeamEntityById(teamId);

        if (!team.getLeaderId().equals(memberId)) {
            throw new YouAreNotTeamLeaderException();
        }

        team.update(teamDTO.name(), teamDTO.description(), encryptedPassword, teamDTO.whenToMeet(),
                teamDTO.maxParticipantCount(), teamDTO.location());

        return team;
    }

    @Transactional
    public void deleteTeam(Long teamId) {
        teamRepository.deleteById(teamId);
    }

    private EncryptedPassword encryptPassword(PlainPassword plainPassword) {
        final String CRITICAL_ENCRYPT_ERROR_MESSAGE = "팀 비밀번호 암호화 중 예상치 못한 에러 발생";
        try {
            return new EncryptedPassword(aesEncryptionUtil.encrypt(plainPassword.value()));
        } catch (Exception e) {
            log.error(CRITICAL_ENCRYPT_ERROR_MESSAGE, e);
            throw new InternalServerException(CRITICAL_ENCRYPT_ERROR_MESSAGE);
        }
    }
}
