package star.home.constants;

import lombok.Getter;

@Getter
public enum Period {
    DAILY, WEEKLY, MONTHLY, YEARLY;

    public static Period from(String text) {
        for (Period b : Period.values()) {
            if (b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("%s는 유효하지 않은 period 입니다.".formatted(text));
    }
}