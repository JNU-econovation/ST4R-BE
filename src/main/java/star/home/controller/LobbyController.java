package star.home.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.common.security.dto.StarUserDetails;
import star.home.board.service.BoardService;
import star.home.dto.request.LobbyRequest;
import star.home.service.LobbyService;
import star.member.dto.MemberInfoDTO;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class LobbyController {

    private final LobbyService lobbyService;

    public void getLobby(
            @Nullable @AuthenticationPrincipal StarUserDetails userDetails,
            @Valid LobbyRequest request,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        MemberInfoDTO memberInfoDTO = (userDetails != null) ? userDetails.getMemberInfoDTO() : null;
        lobbyService.getLobby(memberInfoDTO, request, pageable);
    }
}
