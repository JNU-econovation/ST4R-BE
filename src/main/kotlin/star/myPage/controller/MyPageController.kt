package star.myPage.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import star.common.annotation.ResolvePageable
import star.common.constants.SortField
import star.common.dto.response.CommonResponse
import star.common.security.dto.StarUserDetails
import star.myPage.dto.request.UpdateProfileRequest
import star.myPage.dto.response.MyBoardResponse
import star.myPage.dto.response.MyPageResponse
import star.myPage.service.MyPageService
import star.team.dto.response.GetTeamsResponse

@RestController
@RequestMapping("/my")
class MyPageController(
    private val service: MyPageService
) {
    @GetMapping
    fun getMyPage(@AuthenticationPrincipal userDetails: StarUserDetails): ResponseEntity<MyPageResponse> {
        return ResponseEntity.ok(service.getMyPage(userDetails.memberInfoDTO))
    }

    @GetMapping("/boards")
    fun getMyBoards(
        @AuthenticationPrincipal userDetails: StarUserDetails,
        @ResolvePageable(allowed = [SortField.CREATED_AT]) pageable: Pageable
    ): ResponseEntity<Slice<MyBoardResponse>> {
        return ResponseEntity.ok(service.getMyBoards(userDetails.memberInfoDTO, pageable))
    }


    @GetMapping("/likedBoards")
    fun getLikedBoards(
        @AuthenticationPrincipal userDetails: StarUserDetails,
        @ResolvePageable(allowed = [SortField.CREATED_AT]) pageable: Pageable
    ): ResponseEntity<Slice<MyBoardResponse>> {
        return ResponseEntity.ok(service.getLikedBoards(userDetails.memberInfoDTO.id, pageable))
    }

    @GetMapping("/likedGroups")
    fun getLikedTeams(
        @AuthenticationPrincipal userDetails: StarUserDetails,
        @ResolvePageable(allowed = [SortField.CREATED_AT]) pageable: Pageable
    ): ResponseEntity<Slice<GetTeamsResponse>> {
        return ResponseEntity.ok(service.getLikedTeams(userDetails.memberInfoDTO.id, pageable))
    }

    @PatchMapping("/profile")
    fun updateProfile(
        @AuthenticationPrincipal userDetails: StarUserDetails,
        @Valid @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<CommonResponse> {

        service.updateProfile(userDetails.memberInfoDTO, request)
        return ResponseEntity.ok(CommonResponse.success())

    }
}