package star.home.constants;

public enum Period {
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    YEARLY("yearly");

    private final String value;

    Period(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Period from(String value) {
        for (Period period : Period.values()) {
            if (period.value.equalsIgnoreCase(value)) {
                return period;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 주기 값입니다: " + value);
    }
}