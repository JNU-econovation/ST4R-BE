package star.home.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.common.dto.response.CommonResponse;
import star.common.security.dto.StarUserDetails;
import star.home.board.service.BoardHeartMemberFacadeService;

@RestController
@RequestMapping("/home/boards/{boardId}/likes")
@RequiredArgsConstructor
public class BoardHeartController {

    private final BoardHeartMemberFacadeService boardHeartMemberFacadeService;

    @PostMapping
    public ResponseEntity<CommonResponse> createHeart(
            @PathVariable Long boardId,
            @AuthenticationPrincipal StarUserDetails userDetails
    ) {
        boardHeartMemberFacadeService.createHeart(userDetails.getMemberInfoDTO(), boardId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<CommonResponse> deleteHeart(
            @PathVariable Long boardId,
            @AuthenticationPrincipal StarUserDetails userDetails
    ) {
        boardHeartMemberFacadeService.deleteHeart(userDetails.getMemberInfoDTO(), boardId);
        return ResponseEntity.noContent().build();
    }
}
