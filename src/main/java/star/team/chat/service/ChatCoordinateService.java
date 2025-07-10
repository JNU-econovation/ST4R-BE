package star.team.chat.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.member.dto.MemberInfoDTO;
import star.team.chat.dto.ChatDTO;
import star.team.chat.dto.request.ChatRequest;
import star.team.chat.dto.response.ChatPreviewResponse;
import star.team.chat.dto.response.ChatReadResponse;
import star.team.chat.dto.response.ChatResponse;
import star.team.chat.service.internal.ChatDataService;
import star.team.chat.service.internal.ChatRedisService;
import star.team.chat.service.internal.RedisChatPublisher;
import star.team.service.internal.TeamMemberDataService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatCoordinateService {

    private static final Long ONE_MILLI_SECOND = 1_000_000L;

    private final ChatDataService chatDataService;
    private final ChatRedisService redisService;
    private final TeamMemberDataService teamMemberDataService;
    private final RedisChatPublisher chatPublisher;
    private final ChannelTopic channelTopic;
    private final ChannelTopic previewChannelTopic;



    @Transactional
    public void publishAndSaveChat(Long teamId, ChatRequest request, MemberInfoDTO memberInfoDTO) {

        // DB에 채팅 저장 todo: 비동기로 하기
        ChatDTO savedChatDTO = chatDataService.saveChat(teamId, memberInfoDTO.id(),
                request.message());

        chatPublisher.publishChat(channelTopic, ChatResponse.from(savedChatDTO));

        redisService.markAsRead(teamId, memberInfoDTO.id(),
                savedChatDTO.chattedAt().plusNanos(ONE_MILLI_SECOND));

        publishPreviewToAllMembers(teamId, savedChatDTO);
    }


    @Transactional(readOnly = true)
    public Slice<ChatResponse> getChatHistory(Long teamId, MemberInfoDTO memberInfoDTO,
            Pageable pageable) {

        Page<ChatDTO> chatPage = chatDataService.getChatHistory(teamId, memberInfoDTO, pageable);

        List<ChatResponse> chatResponses = chatPage.getContent().stream()
                .map(ChatResponse::from)
                .toList();

        return new SliceImpl<>(chatResponses, pageable, chatPage.hasNext());
    }

    public void markAsRead(Long teamId, MemberInfoDTO memberInfoDTO) {
        redisService.markAsRead(teamId, memberInfoDTO.id(), LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Slice<ChatReadResponse> getReadCounts(
            Long teamId, MemberInfoDTO memberInfoDTO, Pageable pageable
    ) {
        List<Long> allMemberIdsInTeam = teamMemberDataService.getAllMemberIdInTeam(teamId);

        Map<Long, LocalDateTime> lastReadTimeMap = allMemberIdsInTeam.stream()
                .collect(
                        Collectors.toMap(
                                memberId -> memberId,
                                memberId -> redisService.getLastReadTime(teamId, memberId)
                        )
                );

        Page<ChatDTO> chatPage = chatDataService.getChatHistory(teamId, memberInfoDTO, pageable);

        List<ChatReadResponse> resultChatReads = chatPage.getContent().stream()
                .map(chatDTO ->
                        ChatReadResponse.from(
                                chatDTO,
                                chatDataService.countReaders(chatDTO, lastReadTimeMap)
                        )
                ).toList();

        return new SliceImpl<>(resultChatReads, pageable, chatPage.hasNext());
    }

    @Transactional(readOnly = true)
    public List<ChatPreviewResponse> getPreview(MemberInfoDTO memberInfoDTO) {

        List<Long> allTeamIds = teamMemberDataService.getAllTeamIdByMemberId(memberInfoDTO.id());

        Map<Long, LocalDateTime> teamIdLastReadTimeMap = allTeamIds.stream()
                .collect(
                        Collectors.toMap(
                                teamId -> teamId,
                                teamId -> redisService.getLastReadTime(teamId, memberInfoDTO.id())
                        )
                );

        return teamIdLastReadTimeMap.entrySet().stream()
                .map(
                        entry -> {
                            Long teamId = entry.getKey();
                            LocalDateTime lastReadTime = entry.getValue();
                            Optional<ChatDTO> recentChat =
                                    chatDataService.getRecentChat(teamId, memberInfoDTO);

                            return ChatPreviewResponse.builder()
                                    .teamId(teamId)
                                    .unreadCount(chatDataService.getUnreadChatCount(teamId,
                                            lastReadTime))
                                    .recentMessage(
                                            recentChat.map(chatDTO -> chatDTO.message().getValue())
                                                    .orElse(null)
                                    )
                                    .build();
                        }
                )
                .toList();
    }

    @Transactional
    public void deleteChats(Long teamId) {
        redisService.deleteAllByTeamId(teamId);
        chatDataService.deleteChats(teamId);
    }


    private void publishPreviewToAllMembers(Long teamId, ChatDTO savedChatDTO) {
        List<Long> allMemberIdsInTeam = teamMemberDataService.getAllMemberIdInTeam(teamId);

        allMemberIdsInTeam.forEach(memberId -> {
            LocalDateTime lastReadTime = redisService.getLastReadTime(teamId, memberId);
            ChatPreviewResponse previewResponse = ChatPreviewResponse.builder()
                    .teamId(teamId)
                    .targetMemberId(memberId)
                    .unreadCount(chatDataService.getUnreadChatCount(teamId, lastReadTime))
                    .recentMessage(savedChatDTO.message().getValue())
                    .build();

            chatPublisher.publishChatPreview(previewChannelTopic, previewResponse);
        });
    }
}