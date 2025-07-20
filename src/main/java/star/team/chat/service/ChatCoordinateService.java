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
import star.common.util.CommonTimeUtils;
import star.member.dto.MemberInfoDTO;
import star.team.annotation.AssertTeamMember;
import star.team.chat.dto.ChatDTO;
import star.team.chat.dto.GeneralMessageDTO;
import star.team.chat.dto.UpdateReadTimeMessageDTO;
import star.team.chat.dto.broadcast.ChatBroadcast;
import star.team.chat.dto.response.ChatPreviewResponse;
import star.team.chat.dto.response.ChatReadResponse;
import star.team.chat.dto.send.ChatSend;
import star.team.chat.enums.MessageType;
import star.team.chat.service.internal.ChatDataService;
import star.team.chat.service.internal.ChatRedisService;
import star.team.chat.service.internal.RedisChatPublisher;
import star.team.service.internal.TeamMemberDataService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatCoordinateService {

    private final ChatDataService chatDataService;
    private final ChatRedisService redisService;
    private final TeamMemberDataService teamMemberDataService;
    private final RedisChatPublisher chatPublisher;
    private final ChannelTopic channelTopic;
    private final ChannelTopic previewChannelTopic;
    private final RedisChatPublisher redisChatPublisher;


    @Transactional
    @AssertTeamMember(teamId = "#teamId", memberInfo = "#memberInfoDTO")
    public void publishAndSaveChat(Long teamId, ChatSend chat, MemberInfoDTO memberInfoDTO) {

        // DB에 채팅 저장
        ChatDTO savedChatDTO = chatDataService.saveChat(teamId, memberInfoDTO.id(),
                chat.message());

        chatPublisher.publishChatAsync(
                channelTopic,
                ChatBroadcast.from(MessageType.GENERAL_MESSAGE, GeneralMessageDTO.from(savedChatDTO))
        );

        publishPreviewToAllMembersAsync(teamId, savedChatDTO);
    }


    @Transactional(readOnly = true)
    @AssertTeamMember(teamId = "#teamId", memberInfo = "#memberInfoDTO")
    public Slice<GeneralMessageDTO> getChatHistory(Long teamId, MemberInfoDTO memberInfoDTO,
            Pageable pageable) {

        Page<ChatDTO> chatPage = chatDataService.getChatHistory(teamId, memberInfoDTO, pageable);

        List<GeneralMessageDTO> chatResponses = chatPage.getContent().stream()
                .map(GeneralMessageDTO::from).toList();

        return new SliceImpl<>(chatResponses, pageable, chatPage.hasNext());
    }

    @AssertTeamMember(teamId = "#teamId", memberInfo = "#memberInfoDTO")
    public void markAsReadAndPublishUpdatedReadTime(Long teamId, MemberInfoDTO memberInfoDTO) {

        LocalDateTime readTime = LocalDateTime.now();

        redisService.markAsRead(teamId, memberInfoDTO.id(), readTime);
        UpdateReadTimeMessageDTO updateMessage = UpdateReadTimeMessageDTO.builder()
                .teamId(teamId)
                .updateMemberId(memberInfoDTO.id())
                .updateReadTime(CommonTimeUtils.convertLocalDateTimeToOffsetDateTime(readTime))
                .build();

        redisChatPublisher.publishChatAsync(
                channelTopic, ChatBroadcast.from(MessageType.UPDATE_READ_TIME, updateMessage)
        );
    }

    @Transactional(readOnly = true)
    @AssertTeamMember(teamId = "#teamId", memberInfo = "#memberInfoDTO")
    public List<ChatReadResponse> getLastReadTimesForInitialLoading(
            Long teamId, MemberInfoDTO memberInfoDTO
    ) {
        List<Long> allMemberIdsInTeam = teamMemberDataService.getAllMemberIdInTeam(teamId);

        return allMemberIdsInTeam.stream()
                .map(memberId -> ChatReadResponse.builder()
                        .memberId(memberId)
                        .readTime(
                                CommonTimeUtils.convertLocalDateTimeToOffsetDateTime(
                                        redisService.getLastReadTime(teamId, memberId)
                                )
                        )
                        .build()
                ).toList();
    }

    @Transactional(readOnly = true)
    public List<ChatPreviewResponse> getPreviewForInitialLoading(MemberInfoDTO memberInfoDTO) {

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
                                    .targetMemberId(memberInfoDTO.id())
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

    private void publishPreviewToAllMembersAsync(Long teamId, ChatDTO savedChatDTO) {
        List<Long> allMemberIdsInTeam = teamMemberDataService.getAllMemberIdInTeam(teamId);

        allMemberIdsInTeam.forEach(memberId -> {
            LocalDateTime lastReadTime = redisService.getLastReadTime(teamId, memberId);
            ChatPreviewResponse previewResponse = ChatPreviewResponse.builder()
                    .teamId(teamId)
                    .targetMemberId(memberId)
                    .unreadCount(chatDataService.getUnreadChatCount(teamId, lastReadTime))
                    .recentMessage(savedChatDTO.message().getValue())
                    .build();

            chatPublisher.publishChatPreviewAsync(previewChannelTopic, previewResponse);
        });
    }
}