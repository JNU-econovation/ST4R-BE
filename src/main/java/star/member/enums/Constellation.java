package star.member.enums;

import java.time.LocalDate;
import java.time.MonthDay;
import lombok.Getter;
import star.common.exception.client.BadDataSyntaxException;


public enum Constellation {
    ARIES("양자리", 3, 21, 4, 19),
    TAURUS("황소자리", 4, 20, 5, 20),
    GEMINI("쌍둥이자리", 5, 21, 6, 21),
    CANCER("게자리", 6, 22, 7, 22),
    LEO("사자자리", 7, 23, 8, 22),
    VIRGO("처녀자리", 8, 23, 9, 22),
    LIBRA("천칭자리", 9, 23, 10, 23),
    SCORPIO("전갈자리", 10, 24, 11, 22),
    SAGITTARIUS("사수자리", 11, 23, 12, 24),
    CAPRICORN("염소자리", 12, 25, 1, 19),
    AQUARIUS("물병자리", 1, 20, 2, 18),
    PISCES("물고기자리", 2, 19, 3, 20);

    @Getter
    private final String koreanName;
    private final MonthDay start;
    private final MonthDay end;

    Constellation(String koreanName, int startMonth, int startDay, int endMonth, int endDay) {
        this.koreanName = koreanName;
        this.start = MonthDay.of(startMonth, startDay);
        this.end = MonthDay.of(endMonth, endDay);
    }

    public static Constellation fromDate(LocalDate date) {
        MonthDay md = MonthDay.from(date);
        for (Constellation sign : values()) {
            if (sign.includes(md)) {
                return sign;
            }
        }
        throw new BadDataSyntaxException("날짜가 별자리에 해당하지 않습니다: " + date);
    }

    private boolean includes(MonthDay md) {
        if (start.isBefore(end) || start.equals(end)) {
            return !md.isBefore(start) && !md.isAfter(end);
        }

        return !md.isBefore(start) || !md.isAfter(end);
    }
}
