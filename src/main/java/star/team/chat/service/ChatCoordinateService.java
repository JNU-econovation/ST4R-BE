package star.team.chat.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.member.dto.MemberInfoDTO;
import star.member.exception.MemberNotFoundException;
import star.member.service.MemberService;
import star.team.chat.config.ChatRedisProperties;
import star.team.chat.dto.ChatDTO;
import star.team.chat.dto.request.ChatRequest;
import star.team.chat.dto.response.ChatPreviewResponse;
import star.team.chat.dto.response.ChatReadResponse;
import star.team.chat.dto.response.ChatResponse;
import star.team.chat.exception.server.RedisRangeExceededException;
import star.team.chat.model.vo.Message;
import star.team.chat.service.internal.ChatDataService;
import star.team.chat.service.internal.ChatRedisService;
import star.team.service.internal.TeamDataService;
import star.team.service.internal.TeamMemberDataService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatCoordinateService {

    private final ChatDataService chatDataService;
    private final ChatRedisService redisService;
    private final TeamDataService teamDataService;
    private final ChatRedisProperties chatRedisProperties;
    private final TeamMemberDataService teamMemberDataService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MemberService memberService;

    public ChatResponse saveChatRedis(Long teamId, ChatRequest request,
            MemberInfoDTO memberInfoDTO) {
        LocalDateTime chattedAt = LocalDateTime.now();

        ChatDTO chat = ChatDTO.builder()
                .teamId(teamId)
                .memberInfo(memberInfoDTO)
                .chattedAt(chattedAt)
                .message(Message.builder().value(request.message()).build())
                .build();

        //채팅을 저장한다.
        ChatDTO redisChat = redisService.saveMessage(teamId, chat);

        //채팅을 읽는다.
        redisService.markAsRead(teamId, memberInfoDTO.id(), chattedAt);

        // 팀 멤버들에게 프리뷰 업데이트를 PUSH한다.
        sendPreviewUpdatesToTeamMembers(teamId, redisChat, memberInfoDTO.id());

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
            List<ChatDTO> redisChatDTOs = e.getRedisChats();

            Page<ChatDTO> chatMessageInDb =
                    chatDataService.getChatHistory(teamId, memberInfoDTO, pageable);

            return mergeRedisAndDbChats(
                    redisChatDTOs, chatMessageInDb, pageable.getPageSize(), pageable
            );
        }
    }

    public void markAsRead(Long teamId, MemberInfoDTO memberInfoDTO) {
        redisService.markAsRead(teamId, memberInfoDTO.id(), LocalDateTime.now());
    }

    /**
     * 채팅방 목록 프리뷰 정보를 PUSH 방식으로 전송합니다. 새로운 메시지가 도착했을 때, 해당 팀의 모든 멤버(보낸 사람 제외)에게 업데이트된 프리뷰 정보를
     * 전송합니다.
     *
     * @param teamId     팀 ID
     * @param recentChat 가장 최근의 채팅 정보
     * @param senderId   메시지를 보낸 사람의 ID (알림에서 제외하기 위함)
     */
    private void sendPreviewUpdatesToTeamMembers(Long teamId, ChatDTO recentChat, Long senderId) {
        List<Long> memberIds = teamMemberDataService.getAllMemberIdInTeam(teamId);

        memberIds.stream().filter(memberId -> !memberId.equals(senderId)).forEach(
                memberId -> {

                    // PUSH를 받을 사용자의 정보를 가져옴
                    MemberInfoDTO memberInfo;
                    try {
                        memberInfo = memberService.getMemberById(memberId);
                    } catch (MemberNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    // 해당 사용자의 안 읽은 메시지 수 계산
                    long unreadCount = calculateUnreadCountForUser(teamId, memberId);

                    ChatPreviewResponse previewResponse = ChatPreviewResponse.builder()
                            .teamId(teamId)
                            .unreadCount(unreadCount)
                            .recentMessage(recentChat.message().getValue())
                            .build();

                    // 사용자의 개인 채널로 프리뷰 정보 PUSH
                    messagingTemplate.convertAndSendToUser(
                            memberInfo.email().getValue(), // Principal 이름 (일반적으로 이메일)
                            "/queue/previews",
                            previewResponse
                    );
                }
        );
    }

    private long calculateUnreadCountForUser(Long teamId, Long memberId) {
        LocalDateTime lastReadAt = redisService.getLastReadTime(teamId, memberId);

        // 1. Redis에서 모든 채팅을 가져오기
        List<ChatDTO> redisChats = redisService.getChats(teamId, 0, 0, true);

        // 2. Redis에서 안 읽은 채팅 수를 계산하기
        Long unreadInRedis = redisChats.stream()
                .filter(chat -> chat.chattedAt().isAfter(lastReadAt))
                .count();

        // 3. Redis에 있는 모든 채팅의 redisId를 추출하기
        Set<Long> redisIdsToExclude = redisChats.stream()
                .map(ChatDTO::chatRedisId)
                .collect(Collectors.toSet());

        // 4. DB에서 Redis에 없는 안 읽은 채팅만 계산하기
        Long unreadInDb = chatDataService.getUnreadChatCount(teamId, lastReadAt, redisIdsToExclude);

        return unreadInRedis + unreadInDb;
    }

    /**
     * [최초 로딩용] 사용자가 속한 모든 팀의 채팅방 프리뷰 정보를 조회합니다. 이 메소드는 채팅방 목록 화면에 처음 진입할 때 한 번만 호출되어야 합니다. 이후의 모든
     * 업데이트는 WebSocket PUSH를 통해 실시간으로 이루어집니다.
     */
    public List<ChatPreviewResponse> getPreview(MemberInfoDTO memberInfoDTO) {
        List<Long> teamIds = teamMemberDataService.getAllTeamIdByMemberId(memberInfoDTO.id());

        return teamIds.stream().map(
                teamId -> {
                    LocalDateTime lastReadAt = redisService.getLastReadTime(teamId,
                            memberInfoDTO.id());

                    String recentMessage;

                    // 1. Redis에서 모든 채팅을 가져오기
                    List<ChatDTO> redisChats = redisService.getChats(teamId, 0, 0, true);

                    // 1-1 가장 최근 채팅 가져오기
                    recentMessage = redisChats.isEmpty() ? null
                            : redisChats.getFirst().message().getValue();

                    // 2. Redis에서 안 읽은 채팅 수를 계산하기
                    Long unreadInRedis = redisChats.stream()
                            .filter(chat -> chat.chattedAt().isAfter(lastReadAt))
                            .count();

                    // 3. Redis에 있는 모든 채팅의 redisId를 추출하기
                    Set<Long> redisIdsToExclude = redisChats.stream()
                            .map(ChatDTO::chatRedisId)
                            .collect(Collectors.toSet());

                    // 4. DB에서 Redis에 없는 안 읽은 채팅만 계산하기
                    Long unreadInDb = chatDataService.getUnreadChatCount(teamId, lastReadAt,
                            redisIdsToExclude);

                    if (recentMessage == null) {
                        // 4-1. 만약 redis recent chat이 null이면 db에서 가장 최근 채팅 가져오기
                        Page<ChatDTO> recentChatPage = chatDataService.getChatHistory(
                                teamId, memberInfoDTO,
                                PageRequest.of(
                                        0, 1, Sort.by(Sort.Direction.DESC, "chattedAt"
                                        )
                                )
                        );

                        if (!recentChatPage.getContent().isEmpty()) {
                            recentMessage = recentChatPage.getContent().getFirst()
                                    .message().getValue();
                        }
                    }

                    return ChatPreviewResponse.builder()
                            .teamId(teamId)
                            .recentMessage(recentMessage)
                            .unreadCount(unreadInRedis + unreadInDb)
                            .build();
                }
        ).toList();
    }

    public Slice<ChatReadResponse> getReadCountsByRedisOrDb(Long teamId,
            MemberInfoDTO memberInfoDTO,
            Pageable pageable) {

        List<Long> allMemberIdsInTeam = teamMemberDataService.getAllMemberIdInTeam(teamId);
        List<ChatReadResponse> resultChatReads = new ArrayList<>();

        boolean hasNext;

        try {
            // Redis에서 채팅 조회
            List<ChatDTO> redisChats = redisService.getChats(teamId, pageable.getPageNumber(),
                    pageable.getPageSize(), false);

            List<ChatReadResponse> chatReads = redisChats.stream()
                    .map(chatDTO ->
                            ChatReadResponse.from(chatDTO,
                                    redisService.countReaders(chatDTO, allMemberIdsInTeam)
                            )
                    ).toList();

            resultChatReads = chatReads;
            hasNext = chatReads.size() >= pageable.getPageSize();

        } catch (RedisRangeExceededException e) {
            // Redis에서 못 찾은 채팅이 있다면 DB 조회
            List<ChatDTO> redisChats = e.getRedisChats();

            List<ChatReadResponse> redisChatReads = redisChats.stream()
                    .map(chatDTO ->
                            ChatReadResponse.from(
                                    chatDTO,
                                    redisService.countReaders(chatDTO, allMemberIdsInTeam
                                    )
                            )
                    ).toList();

            Map<Long, LocalDateTime> lastReadTimeMap = new HashMap<>();

            allMemberIdsInTeam.forEach(memberId -> lastReadTimeMap.put(memberId,
                    redisService.getLastReadTime(teamId, memberId))
            );

            List<ChatDTO> pureDbChats =
                    chatDataService.getChatHistory(teamId, memberInfoDTO, pageable)
                            .stream()
                            .filter(chat -> redisChats.stream()
                                    .noneMatch(rc -> rc.chatRedisId()
                                            .equals(chat.chatRedisId())
                                    )
                            )
                            .toList();

            List<ChatReadResponse> dbChatReads = pureDbChats.stream()
                    .map(chatDTO ->
                            ChatReadResponse.from(
                                    chatDTO,
                                    chatDataService.countReaders(chatDTO, lastReadTimeMap)
                            )
                    ).toList();

            int redisChatReadSize = redisChatReads.size();
            int dbNeedCount = pageable.getPageSize() - redisChatReadSize;

            if (dbNeedCount > dbChatReads.size()) {
                dbNeedCount = dbChatReads.size(); // DB 데이터가 부족하면 전부 사용
            }

            resultChatReads.addAll(redisChatReads);
            resultChatReads.addAll(dbChatReads.subList(0, dbNeedCount));
            hasNext = dbChatReads.size() > dbNeedCount;
        }

        return new SliceImpl<>(resultChatReads, pageable, hasNext);
    }

    @Transactional
    public void deleteChats(Long teamId) {
        redisService.deleteAllByTeamId(teamId);
        chatDataService.deleteChats(teamId);
    }

    @Transactional
    @Scheduled(fixedRate = 600000)
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
