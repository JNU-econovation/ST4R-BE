package star.team.chat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {
    GENERAL_MESSAGE("general"),
    UPDATE_READ_TIME("updateReadTime");

    private final String typeString;

}