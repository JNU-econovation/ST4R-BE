package star.common.exception.client;

public class AlreadyHeartedException extends Client409Exception {
    private static final String ERROR_MESSAGE = "이미 좋아요를 하였습니다.";

    public AlreadyHeartedException() {
        super(ERROR_MESSAGE);
    }
}
