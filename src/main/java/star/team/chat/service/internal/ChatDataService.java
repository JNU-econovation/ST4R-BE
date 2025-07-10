package star.team.chat.service.internal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.member.dto.MemberInfoDTO;
import star.team.chat.dto.ChatDTO;
import star.team.chat.exception.client.YouAreNotChatRoomException;
import star.team.chat.model.entity.Chat;
import star.team.chat.repository.ChatRepository;
import star.team.exception.TeamMemberNotFoundException;
import star.team.model.entity.TeamMember;
import star.team.service.internal.TeamMemberDataService;

@Service
@RequiredArgsConstructor
public class ChatDataService {

    private final TeamMemberDataService teamMemberDataService;
    private final ChatRepository chatRepository;

    @Transactional
    public Map<Long, Map<Long, ChatDTO>> saveChatForSyncToDb(List<ChatDTO> redisChats) {
        if (redisChats == null || redisChats.isEmpty()) {
            return Map.of();
        }

        // teamId → redisId → original ChatDTO
        Map<Long, Map<Long, ChatDTO>> teamRedisChatMap = new HashMap<>();
        List<Chat> chatEntities = new ArrayList<>();

        for (ChatDTO dto : redisChats) {
            Long teamId = dto.teamId();
            Long redisId = dto.chatRedisId();

            TeamMember teamMember = getTeamMember(teamId, dto.memberInfo().id());

            Chat chatEntity = Chat.builder()
                    .redisId(redisId)
                    .teamMember(teamMember)
                    .chattedAt(dto.chattedAt())
                    .message(dto.message().getValue())
                    .build();

            chatEntities.add(chatEntity);

            teamRedisChatMap
                    .computeIfAbsent(teamId, id -> new HashMap<>())
                    .put(redisId, dto);
        }

        chatRepository.saveAll(chatEntities);

        for (Chat saved : chatEntities) {
            Long teamId = saved.getTeamMember().getTeam().getId();
            Long redisId = saved.getRedisId();

            ChatDTO original = teamRedisChatMap.get(teamId).get(redisId);

            ChatDTO updated = ChatDTO.builder()
                    .chatDbId(saved.getId())
                    .chatRedisId(redisId)
                    .teamId(teamId)
                    .memberInfo(original.memberInfo())
                    .chattedAt(original.chattedAt())
                    .message(original.message())
                    .build();

            teamRedisChatMap.get(teamId).put(redisId, updated);
        }

        return teamRedisChatMap;
    }


    @Transactional(readOnly = true)
    public Page<ChatDTO> getChatHistory(Long teamId, MemberInfoDTO memberDTO,
            Pageable pageable) {

        //잘못된 teamId 방지
        getTeamMember(teamId, memberDTO.id());

        return chatRepository.getChatsByTeamMemberTeamId(teamId, pageable).map(ChatDTO::from);
    }

    @Transactional(readOnly = true)
    public Integer countReaders(ChatDTO chatDTO, Map<Long, LocalDateTime> lastReadTimeMap) {
        int count = 0;

        for (Map.Entry<Long, LocalDateTime> entry : lastReadTimeMap.entrySet()) {

            Long memberId = entry.getKey();
            LocalDateTime lastReadTime = entry.getValue();

            if (chatRepository.existsByIdAndChattedAtBefore(chatDTO.chatDbId(), lastReadTime)) {
                count++;
            }
        }

        return count;
    }

    @Transactional
    public void deleteChats(Long teamId) {
        chatRepository.deleteChatsByTeamMemberTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public Long getUnreadChatCount(Long teamId, LocalDateTime lastReadAt,
            Set<Long> redisIdsToExclude) {
        if (redisIdsToExclude == null || redisIdsToExclude.isEmpty()) {
            return chatRepository.countByTeamMemberTeamIdAndChattedAtAfter(teamId, lastReadAt);
        }
        return chatRepository.countByTeamMemberTeamIdAndChattedAtAfterAndRedisIdNotIn(teamId,
                lastReadAt, redisIdsToExclude);
    }

    private TeamMember getTeamMember(Long teamId, Long memberId) {
        try {
            return teamMemberDataService.getTeamMemberEntityByIds(teamId, memberId);
        } catch (TeamMemberNotFoundException e) {
            throw new YouAreNotChatRoomException();
        }
    }
}
