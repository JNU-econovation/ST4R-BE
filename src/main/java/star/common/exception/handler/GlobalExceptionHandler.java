package star.common.exception.handler;

import static star.common.constants.CommonConstants.CRITICAL_ERROR_MESSAGE;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import star.common.dto.response.CommonResponse;
import star.common.exception.client.Client403Exception;
import star.common.exception.client.Client409Exception;
import star.common.exception.client.ClientException;
import star.common.exception.server.InternalServerException;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String message = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(ObjectError::getDefaultMessage)
                .orElse("잘못된 요청입니다.");

        return new ResponseEntity<>(CommonResponse.failure(message), HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<CommonResponse> handleInternalServerException(InternalServerException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(CommonResponse.failure(CRITICAL_ERROR_MESSAGE),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Client403Exception.class)
    public ResponseEntity<CommonResponse> handleClient403Exception(Client403Exception e) {
        return new ResponseEntity<>(CommonResponse.failure(e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Client409Exception.class)
    public ResponseEntity<CommonResponse> handleClient409Exception(Client409Exception e) {
        return new ResponseEntity<>(CommonResponse.failure(e.getMessage()), HttpStatus.CONFLICT);
    }


    @ExceptionHandler(ClientException.class)
    public ResponseEntity<CommonResponse> handleClientException(ClientException e) {
        return new ResponseEntity<>(CommonResponse.failure(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse> handleIllegalArgumentException(
            IllegalArgumentException e) {
        return new ResponseEntity<>(CommonResponse.failure(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> handleException(Exception e) {
        log.error(CRITICAL_ERROR_MESSAGE, e);
        return new ResponseEntity<>(CommonResponse.failure(CRITICAL_ERROR_MESSAGE),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
