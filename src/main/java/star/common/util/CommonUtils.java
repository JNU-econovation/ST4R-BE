package star.common.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class CommonUtils {
    private static final ZoneId SERVER_ZONE = ZoneId.systemDefault();

    public static LocalDateTime convertOffsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
        return offsetDateTime.atZoneSameInstant(SERVER_ZONE).toLocalDateTime();
    }

    public static OffsetDateTime convertLocalDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(SERVER_ZONE).toOffsetDateTime();
    }

}
