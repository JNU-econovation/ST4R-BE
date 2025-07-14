package star.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.common.dto.response.CommonResponse;
import star.common.security.dto.StarUserDetails;
import star.member.dto.reqeust.CompleteRegistrationRequest;
import star.member.service.MemberService;

@RestController
@RequestMapping("/completeRegistration")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService service;

    @PatchMapping
    public ResponseEntity<CommonResponse> completeRegistration(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @Valid @RequestBody CompleteRegistrationRequest request
    ) {
        service.completeRegistration(userDetails.getMemberInfoDTO(), request);
        return ResponseEntity.ok(CommonResponse.success());
    }
}
