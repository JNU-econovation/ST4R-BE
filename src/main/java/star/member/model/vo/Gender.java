package star.member.model.vo;


import star.common.exception.client.BadDataSyntaxException;

public enum Gender {
    MAN,
    WOMAN,
    UNKNOWN;

    public static Gender from(String text) {
        for (Gender b : Gender.values()) {
            if (b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new BadDataSyntaxException("%s는 유효하지 않은 gender 입니다.".formatted(text));
    }
}