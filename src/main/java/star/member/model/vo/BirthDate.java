package star.member.model.vo;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import star.common.exception.client.BadDataMeaningException;
import star.common.exception.client.BadDataSyntaxException;

@Embeddable
public record BirthDate(
        LocalDate value
) {

    private static final LocalDate MIN_BIRTH_DATE = LocalDate.of(1900, 1, 1);

    public BirthDate {
        validate(value);
    }

    public static BirthDate deleted() {
        return new BirthDate(MIN_BIRTH_DATE);
    }

    private void validate(LocalDate value) {
        if (value == null) {
            throw new BadDataSyntaxException("생년월일을 입력해주세요");
        }

        if (value.isAfter(LocalDate.now())) {
            throw new BadDataSyntaxException("생년월일은 미래일 수 없습니다.");
        }

        if (value.isBefore(MIN_BIRTH_DATE)) {
            throw new BadDataMeaningException(
                    "생일은 %s 이전일 수 없습니다".formatted(MIN_BIRTH_DATE.toString()));
        }
    }
}
