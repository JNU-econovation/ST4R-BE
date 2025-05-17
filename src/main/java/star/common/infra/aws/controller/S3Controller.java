package star.common.infra.aws.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import star.common.dto.response.CommonResponse;
import star.common.infra.aws.dto.request.PresignRequest;
import star.common.infra.aws.dto.response.PresignResponse;
import star.common.infra.aws.service.S3Service;
import star.common.security.dto.StarUserDetails;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/upload/s3/presigned-url")
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping
    public ResponseEntity<CommonResponse> presign(
            @AuthenticationPrincipal StarUserDetails userDetails,
            @Valid PresignRequest request
    ) {
        String url = s3Service.generatePresignedPutUrl(request, userDetails.getMemberInfoDTO());
        return ResponseEntity.ok(PresignResponse.success(url));
    }

}