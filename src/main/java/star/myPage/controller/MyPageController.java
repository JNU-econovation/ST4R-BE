package star.myPage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.common.annotation.ResolvePageable;
import star.common.constants.SortField;
import star.common.dto.response.CommonResponse;
import star.common.security.dto.StarUserDetails;
import star.myPage.dto.request.UpdateProfileRequest;
import star.myPage.dto.response.MyBoardResponse;
import star.myPage.dto.response.MyPageResponse;
import star.myPage.service.MyPageService;
import star.team.dto.response.GetTeamsResponse;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService service;

    @GetMapping
    public ResponseEntity<MyPageResponse> getMyPage(
            @AuthenticationPrincipal StarUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                service.getMyPage(userDetails.getMemberInfoDTO())
        );
    }

    @GetMapping("/boards")
    public ResponseEntity<Slice<MyBoardResponse>> getMyBoards(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @ResolvePageable(allowed = SortField.CREATED_AT) Pageable pageable) {

        return ResponseEntity.ok(
                service.getMyBoards(userDetails.getMemberInfoDTO(), pageable)
        );
    }

    @GetMapping("/likedBoards")
    public ResponseEntity<Slice<MyBoardResponse>> getLikedBoards(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @ResolvePageable(allowed = SortField.CREATED_AT) Pageable pageable) {
        return ResponseEntity.ok(
                service.getLikedBoards(userDetails.getMemberInfoDTO().id(), pageable));
    }

    @GetMapping("/likedGroups")
    public ResponseEntity<Slice<GetTeamsResponse>> getLikedTeams(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @ResolvePageable(allowed = SortField.CREATED_AT) Pageable pageable) {
        return ResponseEntity.ok(
                service.getLikedTeams(userDetails.getMemberInfoDTO().id(), pageable)
        );
    }

    @PatchMapping("/profile")
    public ResponseEntity<CommonResponse> updateProfile(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        service.updateProfile(userDetails.getMemberInfoDTO(), request);

        return ResponseEntity.ok(CommonResponse.success());
    }
}