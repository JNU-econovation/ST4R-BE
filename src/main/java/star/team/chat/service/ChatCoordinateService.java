package star.team.chat.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.member.dto.MemberInfoDTO;
import star.team.chat.config.ChatRedisProperties;
import star.team.chat.dto.ChatDTO;
import star.team.chat.dto.request.ChatRequest;
import star.team.chat.dto.response.ChatResponse;
import star.team.chat.exception.RedisRangeExceededException;
import star.team.chat.model.vo.Message;
import star.team.chat.service.internal.ChatDataService;
import star.team.chat.service.internal.ChatRedisService;
import star.team.service.internal.TeamDataService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatCoordinateService {

    private final ChatDataService chatDataService;
    private final ChatRedisService redisService;
    private final TeamDataService teamDataService;
    private final ChatRedisProperties chatRedisProperties;

    public ChatResponse saveChatRedis(Long teamId, ChatRequest request,
            MemberInfoDTO memberInfoDTO) {
        LocalDateTime chattedAt = LocalDateTime.now();

        redisService.markAsRead(teamId, memberInfoDTO.id(), chattedAt);

        ChatDTO chat = ChatDTO.builder()
                .teamId(teamId)
                .memberInfo(memberInfoDTO)
                .chattedAt(chattedAt)
                .message(Message.builder().value(request.message()).build())
                .build();

        ChatDTO redisChat = redisService.saveMessage(teamId, chat);

        return ChatResponse.from(redisChat);
    }

    public Slice<ChatResponse> getChatHistory(Long teamId, MemberInfoDTO memberInfoDTO,
            Pageable pageable) {

        try {
            List<ChatResponse> chatResponseList = redisService.getChats(
                            teamId, pageable.getPageNumber(), pageable.getPageSize(), false
                    )
                    .stream()
                    .map(ChatResponse::from)
                    .toList();

            return new SliceImpl<>(
                    chatResponseList, pageable, chatResponseList.size() >= pageable.getPageSize()
            );

        } catch (RedisRangeExceededException e) {
            List<ChatDTO> redisChatDTOs = e.getChatMessages();

            Page<ChatDTO> chatMessageInDb =
                    chatDataService.getChatHistory(teamId, memberInfoDTO, pageable);

            return mergeRedisAndDbChats(
                    redisChatDTOs, chatMessageInDb, pageable.getPageSize(), pageable
            );
        }
    }

    @Transactional
    public void deleteChats(Long teamId) {
        redisService.deleteAllByTeamId(teamId);
        chatDataService.deleteChats(teamId);
    }

    @Transactional
    @Scheduled(fixedRate = 100_000)
    public void syncChatsFromRedisToDb() {
        List<Long> teamIds = teamDataService.getAllTeamIds();
        List<ChatDTO> redisChatsToSync = new ArrayList<>();

        for (Long teamId : teamIds) {
            List<ChatDTO> redisChats = redisService.getChats(teamId, 0,
                    chatRedisProperties.getSyncSize(), true);

            for (ChatDTO redisChat : redisChats) {

                if (redisChat.chatDbId() == null) {
                    redisChatsToSync.add(redisChat);
                }
            }
        }

        Map<Long, Map<Long, ChatDTO>> teamRedisChatMap = chatDataService.saveChatForSyncToDb(
                redisChatsToSync);

        Long updatedCount = redisService.updateChatWithDbIdMap(teamRedisChatMap);

        log.info("Redis -> DB 채팅 {}개 동기화 완료", updatedCount);

    }

    private Slice<ChatResponse> mergeRedisAndDbChats(
            List<ChatDTO> redisChats,
            Page<ChatDTO> dbPage,
            int pageSize,
            Pageable pageable
    ) {
        Set<Long> redisIds = redisChats.stream()
                .map(ChatDTO::chatRedisId)
                .collect(Collectors.toSet());

        List<ChatDTO> dbChats = dbPage.getContent().stream()
                .filter(chat -> !redisIds.contains(chat.chatRedisId()))
                .toList();

        // 필요한 만큼만 DB에서 채워 넣음
        int dbNeedCount = pageSize - redisChats.size();
        if (dbNeedCount > dbChats.size()) {
            dbNeedCount = dbChats.size(); // DB 데이터가 부족하면 전부 사용
        }

        List<ChatDTO> merged = new ArrayList<>(pageSize);
        merged.addAll(redisChats);
        merged.addAll(dbChats.subList(0, dbNeedCount));

        // 변환
        List<ChatResponse> chatResponses = merged.stream()
                .map(ChatResponse::from)
                .toList();

        boolean hasNext = dbChats.size() > dbNeedCount;
        return new SliceImpl<>(chatResponses, pageable, hasNext);
    }

}
