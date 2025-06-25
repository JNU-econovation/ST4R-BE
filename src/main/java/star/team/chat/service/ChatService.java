package star.team.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import star.member.dto.MemberInfoDTO;
import star.team.chat.dto.request.ChatRequest;
import star.team.chat.dto.response.ChatResponse;
import star.team.chat.model.entity.Chat;
import star.team.chat.repository.ChatRepository;
import star.team.model.entity.TeamMember;
import star.team.service.TeamCoordinateService;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final TeamCoordinateService teamCoordinateService;
    private final ChatRepository chatRepository;

    @Transactional
    public ChatResponse saveChat(Long teamId, ChatRequest request, MemberInfoDTO memberInfoDTO) {
        TeamMember teamMember;

        teamMember = teamCoordinateService.getTeamMember(teamId, memberInfoDTO.id());

        Chat newChat = Chat.builder()
                .teamMember(teamMember)
                .message(request.message())
                .build();

        chatRepository.save(newChat);

        return ChatResponse.from(newChat);
    }

    @Transactional(readOnly = true)
    public Page<ChatResponse> getChatHistory(Long teamId, MemberInfoDTO memberDTO,
            Pageable pageable) {

        //잘못된 teamId 방지
        teamCoordinateService.getTeamMember(teamId, memberDTO.id());

        return chatRepository.getChatsByTeamMemberTeamId(teamId, pageable).map(ChatResponse::from);
    }
}
