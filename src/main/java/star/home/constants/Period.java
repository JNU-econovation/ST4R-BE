package star.home.constants;

import lombok.Getter;
import star.common.exception.client.BadDataSyntaxException;

@Getter
public enum Period {
    DAILY, WEEKLY, MONTHLY, YEARLY;

    public static Period from(String text) {
        for (Period b : Period.values()) {
            if (b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new BadDataSyntaxException("%s는 유효하지 않은 period 입니다.".formatted(text));
    }
}