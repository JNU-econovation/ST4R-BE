package star.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import star.common.dto.response.CommonResponse;
import star.common.security.dto.StarUserDetails;
import star.member.dto.reqeust.CompleteRegistrationRequest;
import star.member.dto.response.NicknameExistsResponse;
import star.member.service.MemberService;
import star.member.service.MemberWithdrawService;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService service;
    private final MemberWithdrawService withdrawService;

    @GetMapping("/exists")
    public ResponseEntity<NicknameExistsResponse> existsNickname(@RequestParam String nickname) {
        return ResponseEntity
                .ok(NicknameExistsResponse.success(service.existsByNickname(nickname)));
    }

    @PatchMapping("/completeRegistration")
    public ResponseEntity<CommonResponse> completeRegistration(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @Valid @RequestBody CompleteRegistrationRequest request
    ) {
        service.completeRegistration(userDetails.getMemberInfoDTO(), request);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @DeleteMapping
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal StarUserDetails userDetails) {
        withdrawService.withdraw(userDetails.getMemberInfoDTO());

        return ResponseEntity.noContent().build();
    }
}
