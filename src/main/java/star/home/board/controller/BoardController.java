package star.home.board.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.common.dto.response.CommonResponse;
import star.common.security.dto.StarUserDetails;
import star.home.board.dto.request.BoardRequest;
import star.home.board.dto.response.BoardResponse;
import star.home.board.service.BoardService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home/boards")
@Validated
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponse> getBoard(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @PathVariable Long boardId) {
        boardService.getBoard(userDetails.getMemberInfoDTO(), boardId);
    }

    @PostMapping
    public ResponseEntity<CommonResponse> createBoard(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @RequestBody BoardRequest request) {
        Long boardId = boardService.createBoard(userDetails.getMemberInfoDTO(), request);

        URI location = URI.create("/home/boards/" + boardId);

        return ResponseEntity
                .created(location)
                .body(CommonResponse.success());
    }
}
