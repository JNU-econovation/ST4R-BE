package star.common.infra.aws.service;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import star.common.exception.client.ClientException;
import star.common.infra.aws.dto.request.PresignRequest;
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
            
        } catch (IllegalArgumentException e) {
            log.error("잘못된 인자로 Presigned URL 생성 실패: {}", e.getMessage());
            throw new ClientException("Presigned URL 생성을 위한 잘못된 파라미터: " + e.getMessage());
            
        } catch (AwsServiceException e) {
            // AWS 서비스 예외 (권한 오류, 버킷 없음 등)
            log.error("AWS S3 서비스 오류로 Presigned URL 생성 실패: {}", e.getMessage(), e);
            throw new ClientException("S3 서비스 오류: " + e.getMessage());
            
        } catch (SdkClientException e) {
            // SDK 클라이언트 예외 (네트워크 오류, DNS 실패 등)
            log.error("AWS SDK 클라이언트 오류로 Presigned URL 생성 실패: {}", e.getMessage(), e);
            throw new ClientException("AWS 연결 오류: " + e.getMessage());
            
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.error("Presigned URL 생성 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new ClientException("파일 업로드 URL 생성 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 객체 키가 유효한지 검사합니다.
     * 추가적인 유효성 검사가 필요할 경우 이 메서드를 확장하세요.
     * 
     * @param objectKey 검사할 객체 키
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    private boolean isValidObjectKey(String objectKey) {
        return objectKey != null && !objectKey.trim().isEmpty();
    }
}