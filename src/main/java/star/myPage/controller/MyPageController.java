package star.myPage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.common.annotation.ResolvePageable;
import star.common.constants.SortField;
import star.common.security.dto.StarUserDetails;
import star.myPage.dto.response.MyBoardResponse;
import star.myPage.service.MyPageService;
import star.team.dto.response.GetMyTeamsResponse;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/boards")
    public ResponseEntity<Slice<MyBoardResponse>> getMyBoards(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @ResolvePageable(allowed = SortField.CREATED_AT) Pageable pageable) {

        return ResponseEntity.ok(
                myPageService.getMyBoards(userDetails.getMemberInfoDTO(), pageable)
        );
    }

    @GetMapping("/likedBoards")
    public ResponseEntity<Slice<MyBoardResponse>> getLikedBoards(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @ResolvePageable(allowed = SortField.CREATED_AT) Pageable pageable) {
        return ResponseEntity.ok(
                myPageService.getLikedBoards(userDetails.getMemberInfoDTO().id(), pageable));
    }

    @GetMapping("/pickedTeams")
    public ResponseEntity<Slice<GetMyTeamsResponse>> getLikedTeams(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @ResolvePageable(allowed = SortField.CREATED_AT) Pageable pageable) {
        return ResponseEntity.ok(
                myPageService.getLikedTeams(userDetails.getMemberInfoDTO().id(), pageable)
        );
    }
}