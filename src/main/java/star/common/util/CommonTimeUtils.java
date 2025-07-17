package star.common.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import star.common.dto.LocalDateTimesDTO;
import star.home.constants.Period;

@Slf4j
public final class CommonTimeUtils {

    private static final ZoneId SERVER_ZONE = ZoneId.of("UTC");

    public static LocalDateTime convertOffsetDateTimeToLocalDateTime(
            OffsetDateTime offsetDateTime) {
        return offsetDateTime.atZoneSameInstant(SERVER_ZONE).toLocalDateTime();
    }

    public static OffsetDateTime convertLocalDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime.atOffset(ZoneOffset.UTC);
    }

    public static LocalDateTimesDTO getLocalDateTimesByPeriod(Period period) {
        LocalDateTime start, end;

        switch (period) {
            case DAILY -> {
                start = LocalDate.now().atStartOfDay();
                end = start.plusDays(1);
            }

            case WEEKLY -> {
                LocalDate today = LocalDate.now();
                LocalDate monday = today.with(DayOfWeek.MONDAY);
                start = monday.atStartOfDay();
                end = start.plusWeeks(1);
            }

            case MONTHLY -> {
                LocalDate today = LocalDate.now();
                LocalDate firstDayOfMonth = today.withDayOfMonth(1);
                start = firstDayOfMonth.atStartOfDay();
                end = start.plusMonths(1);
            }

            case YEARLY -> {
                LocalDate today = LocalDate.now();
                LocalDate firstDayOfYear = today.withDayOfYear(1);
                start = firstDayOfYear.atStartOfDay();
                end = start.plusYears(1);
            }

            default -> {
                LocalDate today = LocalDate.now();
                LocalDate firstDayOfYear = today.withDayOfYear(1);
                start = firstDayOfYear.atStartOfDay();
                end = start.plusYears(1);

                log.warn("Period 값이 예상치 못한 값이라 일단은 Yearly로 적용\n입력된 Period 값 -> {}",
                        Objects.requireNonNull(period, "null"));
            }
        }

        return LocalDateTimesDTO.builder().start(start).end(end).build();
    }

}
