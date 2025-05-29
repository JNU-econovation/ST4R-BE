package star.common.exception.handler;

import static star.common.constants.CommonConstants.CRITICAL_ERROR_MESSAGE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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
        log.warn("Missing parameter: {}", parameterName);
        return new ResponseEntity<>(CommonResponse.failure(message), status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        StringBuilder errorMessage = new StringBuilder();

        String firstError = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(ObjectError::getDefaultMessage)
                .orElse("잘못된 요청입니다.");

        errorMessage.append(firstError);

        // for 디버깅
        if (log.isDebugEnabled()) {
            ex.getBindingResult().getAllErrors().forEach(error -> {
                if (error instanceof FieldError fieldError) {
                    log.debug("Validation error - Field: {}, Value: {}, Message: {}",
                            fieldError.getField(), fieldError.getRejectedValue(),
                            error.getDefaultMessage());
                } else {
                    log.debug("Validation error - Message: {}", error.getDefaultMessage());
                }
            });
        }

        return new ResponseEntity<>(CommonResponse.failure(errorMessage.toString()),
                HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String message = "잘못된 JSON 형식입니다.";
        Throwable cause = ex.getCause();

        if (cause instanceof JsonProcessingException jsonEx) {
            log.error("JSON parsing error: {}", jsonEx.getMessage());

            if (jsonEx instanceof InvalidFormatException invalidFormatEx) {
                String fieldName = invalidFormatEx.getPath().isEmpty() ? "unknown" :
                        invalidFormatEx.getPath().get(0).getFieldName();
                message = String.format("필드 '%s'의 값이 올바르지 않습니다: %s",
                        fieldName, invalidFormatEx.getValue());
            } else if (jsonEx instanceof MismatchedInputException mismatchedEx) {
                String fieldName = mismatchedEx.getPath().isEmpty() ? "unknown" :
                        mismatchedEx.getPath().get(0).getFieldName();
                message = String.format("필드 '%s'의 타입이 맞지 않습니다.", fieldName);
            } else if (jsonEx instanceof JsonMappingException) {
                message = "JSON 매핑 오류가 발생했습니다: " + jsonEx.getOriginalMessage();
            }
        } else {
            log.error("HTTP message not readable: {}", ex.getMessage());
            message = "요청 본문을 읽을 수 없습니다. JSON 형식을 확인해주세요.";
        }

        return new ResponseEntity<>(CommonResponse.failure(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CommonResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        String message = String.format("파라미터 '%s'의 값 '%s'이(가) 올바르지 않습니다.",
                ex.getName(), ex.getValue());
        log.warn("Type mismatch for parameter: {} with value: {}", ex.getName(), ex.getValue());

        return new ResponseEntity<>(CommonResponse.failure(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<CommonResponse> handleJsonProcessingException(
            JsonProcessingException ex) {
        log.error("JSON processing error: {}", ex.getMessage());
        String message = "JSON 처리 중 오류가 발생했습니다: " + ex.getOriginalMessage();
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

    @ExceptionHandler(BeanInstantiationException.class)
    public ResponseEntity<CommonResponse> handleBeanInstantiationException(
            BeanInstantiationException e) {

        Throwable cause = NestedExceptionUtils.getMostSpecificCause(e);

        //rootCause 니까 instanceof 말고 getclass 로 하기
        if (cause.getClass().equals(IllegalArgumentException.class)) {
            return new ResponseEntity<>(CommonResponse.failure(cause.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }

        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(CommonResponse.failure("요청 파라미터가 잘못되었습니다."),
                HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> handleException(Exception e) {
        log.error(CRITICAL_ERROR_MESSAGE, e);
        return new ResponseEntity<>(CommonResponse.failure(CRITICAL_ERROR_MESSAGE),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
