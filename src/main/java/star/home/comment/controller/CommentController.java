package star.home.comment.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.common.dto.response.CommonResponse;
import star.common.security.dto.StarUserDetails;
import star.home.comment.dto.request.CommentRequest;
import star.home.comment.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home/boards")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{boardId}")
    public ResponseEntity<CommonResponse> createComment(
            @PathVariable Long boardId,
            @AuthenticationPrincipal StarUserDetails userDetails,
            @RequestBody CommentRequest request
    ) {
        Long commentId = commentService.createComment(userDetails.getMemberInfoDTO(), boardId, request);
        URI location = URI.create("/home/boards/" + boardId + "/comments/" + commentId);

        return ResponseEntity
                .created(location)
                .body(CommonResponse.success());
    }

}
