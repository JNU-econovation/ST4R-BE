package star.member.dto.response;

import lombok.Getter;
import star.common.dto.response.CommonResponse;

@Getter
public class NicknameExistsResponse extends CommonResponse {
    private final boolean exists;

    private NicknameExistsResponse(boolean exists) {
        super("SUCCESS");
        this.exists = exists;
    }

    public static NicknameExistsResponse success(boolean exists) {
        return new NicknameExistsResponse(exists);
    }
}
