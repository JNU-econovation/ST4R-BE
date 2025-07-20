package star.team.chat.service.internal;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.member.dto.MemberInfoDTO;
import star.team.annotation.AssertTeamMember;
import star.team.chat.dto.ChatDTO;
import star.team.chat.exception.client.YouAreNotChatRoomException;
import star.team.chat.model.entity.Chat;
import star.team.chat.repository.ChatRepository;
import star.team.model.entity.TeamMember;
import star.team.service.internal.TeamMemberDataService;

@Service
@RequiredArgsConstructor
public class ChatDataService {

    private final TeamMemberDataService teamMemberDataService;
    private final ChatRepository chatRepository;

    @Transactional
    public ChatDTO saveChat(Long teamId, Long memberId, String message) {
        TeamMember teamMember = getTeamMember(teamId, memberId);

        Chat chatEntity = Chat.builder()
                .teamMember(teamMember)
                .chattedAt(LocalDateTime.now())
                .message(message)
                .build();

        return ChatDTO.from(chatRepository.save(chatEntity));
    }

    @Transactional(readOnly = true)
    public Optional<ChatDTO> getRecentChat(Long teamId, MemberInfoDTO memberDTO) {

        Page<ChatDTO> recentChatDTOPage = getChatHistory(teamId, memberDTO,
                PageRequest.of(
                        0, 1, Sort.by(Sort.Direction.DESC, "chattedAt"
                        )
                )
        );

        if (recentChatDTOPage.getContent().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(recentChatDTOPage.getContent().getFirst());
    }

    @Transactional(readOnly = true)
    @AssertTeamMember(teamId = "#teamId", memberInfo = "#memberDTO")
    public Page<ChatDTO> getChatHistory(Long teamId, MemberInfoDTO memberDTO, Pageable pageable) {

        return chatRepository.getChatsByTeamMemberTeamId(teamId, pageable).map(ChatDTO::from);
    }

    @Transactional
    public void deleteChats(Long teamId) {
        chatRepository.deleteChatsByTeamMemberTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public Long getUnreadChatCount(Long teamId, LocalDateTime lastReadAt) {
        return chatRepository.countByTeamMemberTeamIdAndChattedAtGreaterThanEqual(teamId, lastReadAt);
    }

    private TeamMember getTeamMember(Long teamId, Long memberId) {

        return teamMemberDataService.getOptionalTeamMemberEntityByIds(teamId, memberId)
                .orElseThrow(YouAreNotChatRoomException::new);
    }
}
