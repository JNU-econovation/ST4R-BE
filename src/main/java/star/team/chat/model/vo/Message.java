package star.team.chat.model.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Message {
    private String value;

    private static final int MAX_MESSAGE_LENGTH = 10000;

    

    private void validateMessage(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("메세지를 입력해주세요.");
        }

        if (value.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException(
                    "메세지를 %d자 이내로 작성해주세요.".formatted(MAX_MESSAGE_LENGTH));
        }

    }

}
