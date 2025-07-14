package star.common.infra.aws.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import star.common.infra.aws.dto.request.PresignRequest;
import star.common.infra.exception.S3UnknownException;
import star.member.dto.MemberInfoDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private static final Integer EXPIRATION_MINUTES = 10;

    private final S3Presigner presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String generatePresignedPutUrl(PresignRequest request, MemberInfoDTO memberInfoDTO) {
        String objectKey = memberInfoDTO.id() + "-" + request.fileName();
        Duration expiration = Duration.ofMinutes(EXPIRATION_MINUTES);

        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(expiration)
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toString();
            
        } catch (AwsServiceException e) {
            log.error("AWS S3 서비스 오류로 Presigned URL 생성 실패: {}", e.getMessage(), e);
            throw new S3UnknownException();
            
        } catch (SdkClientException e) {
            log.error("AWS SDK 클라이언트 오류로 Presigned URL 생성 실패: {}", e.getMessage(), e);
            throw new S3UnknownException();
            
        } catch (Exception e) {
            log.error("Presigned URL 생성 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new S3UnknownException();
        }
    }
}