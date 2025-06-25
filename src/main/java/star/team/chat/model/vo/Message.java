package star.team.chat.model.vo;

public record Message(String value) {

    private static final int MAX_MESSAGE_LENGTH = 10000;

    public Message {
        validateMessage(value);
    }

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
