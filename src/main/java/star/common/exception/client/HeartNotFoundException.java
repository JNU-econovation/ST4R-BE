package star.common.exception.client;

public class HeartNotFoundException extends Client409Exception {
    private static final String ERROR_MESSAGE = "이미 좋아요를 취소하였습니다.";

    public HeartNotFoundException() {
        super(ERROR_MESSAGE);
    }
}
