package star.home.dto.request;

import java.util.Arrays;
import java.util.List;

public record LobbyRequest(
        String period
) {

    private static final List<String> PERIOD_FIELD = Arrays.asList("daily", "weekly", "monthly",
            "yearly");

    public LobbyRequest {
        validate(period);
    }

    private void validate(String period) {
        if (!PERIOD_FIELD.contains(period)) {
            throw new IllegalArgumentException("period는 %s 중에 하나여야합니다.".formatted(PERIOD_FIELD.toString()));
        }
    }
}
