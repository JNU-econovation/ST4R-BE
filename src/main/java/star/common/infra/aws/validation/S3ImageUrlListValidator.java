package star.common.infra.aws.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import star.common.infra.aws.annotation.S3ImageUrl;

public class S3ImageUrlListValidator implements ConstraintValidator<S3ImageUrl, List<String>> {

    private static final String ALLOWED_PREFIX = "https://st4rbucket.s3.ap-northeast-2.amazonaws.com/";

    @Override
    public boolean isValid(List<String> values, ConstraintValidatorContext context) {
        if (values == null || values.isEmpty()) {
            return true;
        }

        for (String value : values) {
            if (value == null || !value.startsWith(ALLOWED_PREFIX)) {
                return false;
            }
        }

        return true;
    }
}

