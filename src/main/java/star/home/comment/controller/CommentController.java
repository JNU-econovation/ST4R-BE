package star.home.comment.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import star.home.comment.dto.request.CommentRequest;
import star.home.comment.dto.response.CommentResponse;
import star.home.comment.service.CreateCommentFacadeService;
import star.home.comment.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home/boards/{boardId}/comments")
public class CommentController {

    private final CreateCommentFacadeService createCommentFacadeService;
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommonResponse> createComment(
            @PathVariable Long boardId,
            @AuthenticationPrincipal StarUserDetails userDetails,
            @RequestBody CommentRequest request
    ) {
        Long commentId = createCommentFacadeService.createComment(userDetails.getMemberInfoDTO(),
                request, boardId);
        URI location = URI.create("/home/boards/" + boardId + "/comments/" + commentId);

        return ResponseEntity.created(location).body(CommonResponse.success());
    }

    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getComments(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @PathVariable Long boardId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(
                commentService.getCommentsPage(userDetails.getMemberInfoDTO(), boardId, pageable));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommonResponse> updateComment(
            @PathVariable Long boardId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal StarUserDetails userDetails,
            @RequestBody CommentRequest request
    ) {
        commentService.updateComment(boardId, commentId, userDetails.getMemberInfoDTO(), request);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommonResponse> softDeleteComment(
            @PathVariable Long boardId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal StarUserDetails userDetails
    ) {
        commentService.softDeleteComment(boardId, commentId, userDetails.getMemberInfoDTO());
        return ResponseEntity.noContent().build();
    }

}
