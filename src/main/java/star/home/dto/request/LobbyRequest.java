package star.home.dto.request;


import static star.common.constants.CommonConstants.PERIOD_FIELDS;

public record LobbyRequest(
        String period
) {
    public LobbyRequest {
        validate(period);
    }

    private void validate(String period) {
        if (!PERIOD_FIELDS.contains(period)) {
            throw new IllegalArgumentException("period는 %s 중에 하나여야합니다.".formatted(PERIOD_FIELDS.toString()));
        }
    }
}
