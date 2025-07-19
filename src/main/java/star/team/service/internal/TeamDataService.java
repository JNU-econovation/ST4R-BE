package star.team.service.internal;

import static star.team.constants.TeamConstants.PARTICIPANT_MIN_CAPACITY;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.common.security.exception.EncryptionException;
import star.common.util.CommonTimeUtils;
import star.member.model.entity.Member;
import star.team.dto.CreateTeamDTO;
import star.team.dto.TeamSearchDTO;
import star.team.dto.UpdateTeamDTO;
import star.team.exception.NewPasswordSameAsOldException;
import star.team.exception.TeamNotFoundException;
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
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Team createTeam(Member leader, CreateTeamDTO createTeamDTO) {
        EncryptedPassword encryptedPassword =
                (createTeamDTO.plainPassword() == null) ? null
                        : encryptPassword(createTeamDTO.plainPassword());

        Team team = Team.builder()
                .name(createTeamDTO.name())
                .description(createTeamDTO.description())
                .encryptedPassword(encryptedPassword)
                .leader(leader)
                .participant(Participant.builder()
                        .current(PARTICIPANT_MIN_CAPACITY)
                        .capacity(createTeamDTO.maxParticipantCount())
                        .build())
                .whenToMeet(
                        CommonTimeUtils.convertOffsetDateTimeToLocalDateTime(
                                createTeamDTO.whenToMeet()))
                .location(createTeamDTO.location())
                .build();

        return teamRepository.save(team);
    }

    @Transactional(readOnly = true)
    public Page<Team> getTeams(TeamSearchDTO searchDTO, Pageable pageable) {
        return teamRepository.searchTeams(searchDTO, pageable);
    }



    @Transactional(readOnly = true)
    public Team getTeamEntityById(Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(TeamNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<Long> getAllTeamIds() {
        return teamRepository.getAllTeamIds();
    }

    @Transactional
    public Team updateTeam(Team team, UpdateTeamDTO updateTeamDTO) {
        EncryptedPassword encryptedPassword =
                (updateTeamDTO.plainPassword() == null) ? null
                        : encryptPassword(updateTeamDTO.plainPassword());

        if (team.getEncryptedPassword() != null
                && team.getEncryptedPassword() == encryptedPassword) {
            throw new NewPasswordSameAsOldException();
        }

        LocalDateTime overwrittenWhenToMeet =
                updateTeamDTO.newWhenToMeet() == null ? team.getWhenToMeet()
                        : CommonTimeUtils.convertOffsetDateTimeToLocalDateTime(
                                updateTeamDTO.newWhenToMeet());

        team.update(updateTeamDTO.name(), updateTeamDTO.description(), encryptedPassword,
                overwrittenWhenToMeet,
                updateTeamDTO.maxParticipantCount(), updateTeamDTO.location());

        return team;
    }

    @Transactional
    public void deleteTeam(Long teamId) {
        teamRepository.deleteById(teamId);
    }

    private EncryptedPassword encryptPassword(PlainPassword plainPassword) {
        final String CRITICAL_ENCRYPT_ERROR_MESSAGE = "팀 비밀번호 암호화 중 예상치 못한 에러 발생";
        try {
            return new EncryptedPassword(passwordEncoder.encode(plainPassword.value()));
        } catch (Exception e) {
            log.error(CRITICAL_ENCRYPT_ERROR_MESSAGE, e);
            throw new EncryptionException();
        }
    }
}