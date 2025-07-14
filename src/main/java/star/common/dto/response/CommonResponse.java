package star.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import star.common.exception.ErrorCode;

@Getter
public class CommonResponse {

    private static final String SUCCESS_MESSAGE = "SUCCESS";

    private final Boolean isSuccess;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String errorCode;

    protected CommonResponse(String message) {
        this.isSuccess = true;
        this.message = message;
        this.errorCode = null;
    }

    private CommonResponse(ErrorCode errorCode) {
        this.isSuccess = false;
        this.message = errorCode.getMessage();
        this.errorCode = errorCode.getCode();
    }
    
    private CommonResponse(ErrorCode errorCode, String message) {
        this.isSuccess = false;
        this.message = message;
        this.errorCode = errorCode.getCode();
    }

    public static CommonResponse success() {
        return new CommonResponse(SUCCESS_MESSAGE);
    }

    public static CommonResponse failure(ErrorCode errorCode) {
        return new CommonResponse(errorCode);
    }
    
    public static CommonResponse failure(ErrorCode errorCode, String message) {
        return new CommonResponse(errorCode, message);
    }
}
