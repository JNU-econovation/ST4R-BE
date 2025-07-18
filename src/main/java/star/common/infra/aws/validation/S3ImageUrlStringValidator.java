package star.common.infra.aws.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import star.common.infra.aws.annotation.S3ImageUrl;

public class S3ImageUrlStringValidator implements ConstraintValidator<S3ImageUrl, String> {

    private static final String ALLOWED_PREFIX = "https://st4rbucket.s3.ap-northeast-2.amazonaws.com/";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        return value.startsWith(ALLOWED_PREFIX);
    }
}
