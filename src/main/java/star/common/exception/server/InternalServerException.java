package star.common.exception.server;

import org.springframework.core.NestedRuntimeException;

public class InternalServerException extends NestedRuntimeException {
    public InternalServerException(String message) {
        super(message);
    }
}
