package star.common.infra.aws.dto.response;

import lombok.Getter;
import star.common.dto.response.CommonResponse;

@Getter
public class PresignResponse extends CommonResponse {
    private final String presignedUrl;

    private PresignResponse(String presignedUrl) {
        super("SUCCESS");
        this.presignedUrl = presignedUrl;
    }

    public static PresignResponse success(String presignedUrl) {
        return new PresignResponse(presignedUrl);
    }
}