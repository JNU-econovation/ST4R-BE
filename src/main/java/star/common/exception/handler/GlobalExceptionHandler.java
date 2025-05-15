package star.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import star.common.auth.exception.InvalidRedirectUriException;
import star.common.dto.response.CommonResponse;
import star.common.exception.InternalServerException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String CRITICAL_ERROR_MESSAGE = "서버에서 예상치 못한 에러가 발생하였습니다.";

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String parameterName = ex.getParameterName();
        String message = "필요로 하는 파라미터 -> " + parameterName + " 이(가) 없습니다.";
        return new ResponseEntity<>(CommonResponse.failure(message), status);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<CommonResponse> handleInternalServerException(InternalServerException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(CommonResponse.failure(CRITICAL_ERROR_MESSAGE),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidRedirectUriException.class)
    public ResponseEntity<CommonResponse> handleInvalidRedirectUriException(InvalidRedirectUriException e) {
        return new ResponseEntity<>(CommonResponse.failure(e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> handleException(Exception e) {
        log.error(CRITICAL_ERROR_MESSAGE, e);
        return new ResponseEntity<>(CommonResponse.failure(CRITICAL_ERROR_MESSAGE),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
