package star.common.dto.response;

import lombok.Getter;

@Getter
public class CommonResponse {

    private static final String SUCCESS_MESSAGE = "SUCCESS";

    private final Boolean isError;
    private final String message;

    private CommonResponse(Boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

    public static CommonResponse success() {
        return new CommonResponse(false, SUCCESS_MESSAGE);
    }

    public static CommonResponse failure(String message) {
        return new CommonResponse(true, message);
    }
}
