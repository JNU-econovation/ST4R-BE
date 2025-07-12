package star.team.chat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {
    GENERAL_MESSAGE("general"),
    UPDATE_READ_TIME("updateReadTime");

    private final String typeString;

    public static MessageType fromString(String typeString) {
        for (MessageType type : values()) {
            if (type.typeString.equals(typeString)) {
                return type;
            }
        }
        throw new IllegalArgumentException("알려지지 않은 타입입니다 -> " + typeString);
    }
}
