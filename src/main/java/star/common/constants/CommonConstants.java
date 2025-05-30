package star.common.constants;

import java.util.Arrays;
import java.util.List;

public final class CommonConstants {
    public static final String CRITICAL_ERROR_MESSAGE = "서버에서 예상치 못한 에러가 발생하였습니다.";
    public static final int OPTIMISTIC_ATTEMPT_COUNT = 10;
    public static final int MAX_IMAGE_COUNT = 10;
    public static final long ANONYMOUS_MEMBER_ID = -12345678L;
    public static final List<String> PERIOD_FIELDS = Arrays.asList("daily", "weekly", "monthly",
            "yearly");
}