package star.home.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.common.security.dto.StarUserDetails;
import star.home.constants.Period;
import star.home.dto.request.LobbyRequest;
import star.home.dto.response.LobbyBoardResponse;
import star.home.service.LobbyService;
import star.member.dto.MemberInfoDTO;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class LobbyController {

    private final LobbyService lobbyService;

    //todo: 혹시라도 로비에서 board 말고 다른것도 응답해야 한다면 uri 리팩터링 하기
    public ResponseEntity<LobbyBoardResponse> getLobbyBoards(
            @Nullable @AuthenticationPrincipal StarUserDetails userDetails,
            @Valid LobbyRequest request,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        MemberInfoDTO memberInfoDTO = (userDetails != null) ? userDetails.getMemberInfoDTO() : null;

        return ResponseEntity.ok(
                lobbyService.getLobbyBoards(memberInfoDTO, Period.from(request.period()),
                        pageable));

    }
}
