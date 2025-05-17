package star.home.board.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.common.dto.response.CommonResponse;
import star.common.security.dto.StarUserDetails;
import star.home.board.dto.request.BoardRequest;
import star.home.board.dto.response.BoardResponse;
import star.home.board.service.BoardService;
import star.member.dto.MemberInfoDTO;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/home/boards")
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<CommonResponse> createBoard(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @Valid @RequestBody BoardRequest request) {
        Long boardId = boardService.createBoard(userDetails.getMemberInfoDTO(), request);

        URI location = URI.create("/home/boards/" + boardId);

        return ResponseEntity
                .created(location)
                .body(CommonResponse.success());
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponse> getBoard(
            @Nullable @AuthenticationPrincipal StarUserDetails userDetails,
            @PathVariable Long boardId) {
        MemberInfoDTO memberInfoDTO = (userDetails != null) ? userDetails.getMemberInfoDTO() : null;
        return ResponseEntity.ok(boardService.getBoard(memberInfoDTO, boardId));
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<CommonResponse> updateBoard(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @PathVariable Long boardId,
            @Valid @RequestBody BoardRequest request) {
        boardService.updateBoard(userDetails.getMemberInfoDTO(), boardId, request);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<CommonResponse> deleteBoard(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @PathVariable Long boardId) {
        boardService.deleteBoard(userDetails.getMemberInfoDTO(), boardId);
        return ResponseEntity.noContent().build();
    }
}
