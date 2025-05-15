package star.home.board.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.common.dto.response.CommonResponse;
import star.home.board.dto.request.BoardRequest;
import star.home.board.service.BoardService;
import star.member.dto.MemberInfoDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home/boards")
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<CommonResponse> createBoard(
            @AuthenticationPrincipal MemberInfoDTO memberInfoDTO, BoardRequest request) {
        Long boardId = boardService.createBoard(memberInfoDTO, request);

        URI location = URI.create("/home/boards/" + boardId);

        return ResponseEntity
                .created(location)
                .body(CommonResponse.success());
    }
}
