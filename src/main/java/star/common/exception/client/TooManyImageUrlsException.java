package star.common.exception.client;

import lombok.Getter;
import star.common.exception.ErrorCode;

@Getter
public class TooManyImageUrlsException extends ClientException {

    private final int maxImageUrls;

    public TooManyImageUrlsException(int maxImageUrls) {
        super(ErrorCode.TOO_MANY_IMAGE_URLS);
        this.maxImageUrls = maxImageUrls;
    }

    @Override
    public String getMessage() {
        return String.format(getErrorCode().getMessage(), maxImageUrls);
    }
}
