package star.myPage.dto.request;

import static star.constants.MemberConstants.NICKNAME_MAX_LENGTH;
import static star.constants.MemberConstants.NICKNAME_MIN_LENGTH;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import star.common.exception.client.BadDataSyntaxException;

public record UpdateProfileRequest(

        @Nullable
        String profileImageUrlToChange,

        @Nullable
        @Size(min = NICKNAME_MIN_LENGTH, max = NICKNAME_MAX_LENGTH, message = "닉네임의 길이는 최소 {min}자, 최대 {max}자여야 합니다.")
        String nicknameToChange,

        @NotNull(message = "changeProfileImage를 입력해주세요")
        boolean changeProfileImage,

        @NotNull(message = "changeNickname 입력해주세요")
        boolean changeNickname
) {

    @JsonCreator
    public UpdateProfileRequest {
        validate(profileImageUrlToChange, nicknameToChange, changeProfileImage, changeNickname);
    }

    private void validate(
            String profileImageUrlToChange, String nicknameToChange, boolean changeProfileImage,
            boolean changeNickname
    ) {
        if (changeProfileImage && profileImageUrlToChange == null) {
            throw new BadDataSyntaxException(
                    "changeProfileImage가 true이면 profileImageUrlToChange를 입력해주세요.");
        }

        if (changeNickname && nicknameToChange == null) {
            throw new BadDataSyntaxException("changeNickname이 true이면 nicknameToChange를 입력해주세요.");
        }

        if (!changeNickname && !changeProfileImage) {
            throw new BadDataSyntaxException(
                    "changeNickname과 changeProfileImage 둘 다 false일 수 없습니다.");
        }
    }

}
