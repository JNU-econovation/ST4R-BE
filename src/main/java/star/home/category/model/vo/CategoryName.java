package star.home.category.model.vo;

public enum CategoryName {
    SPOT, GENERAL, PROMOTION;

    public static CategoryName from(String text) {
        for (CategoryName b : CategoryName.values()) {
            if (b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        String validCategories = String.join(", ",
                SPOT.name().toLowerCase(),
                GENERAL.name().toLowerCase(),
                PROMOTION.name().toLowerCase()
        );

        throw new IllegalArgumentException(
                "'%s'는 유효하지 않은 카테고리입니다. '%s' 중에 하나를 입력해 주세요.".formatted(text, validCategories)
        );
    }
}
