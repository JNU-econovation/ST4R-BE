package star.common.exception.client;

public class TooManyImageUrlsException extends ClientException {

    public TooManyImageUrlsException(Integer maxImageUrls) {
        super("이미지는 최대 %d개 까지 가능합니다.".formatted(maxImageUrls));
    }
}
