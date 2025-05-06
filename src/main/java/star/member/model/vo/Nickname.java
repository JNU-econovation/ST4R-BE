package star.member.model.vo;

public record Nickname(String value) {

//    public Nickname {
//        validateNickname(value);
//    }
//todo: 닉네임 최대 길이 정하기

//    private void validateNickname(String value) {
//        if (value == null || value.isBlank()) {
//            throw new IllegalArgumentException("닉네임을 입력해주세요.");
//        }
//
//        if (value.length() > 32) {
//            throw new IllegalArgumentException("닉네임의 길이는 최대 32자 입니다.");
//        }
//    }

}