package star.common.infra.aws.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import star.common.infra.aws.validation.S3ImageUrlListValidator;
import star.common.infra.aws.validation.S3ImageUrlStringValidator;

@Documented
@Constraint(validatedBy = {
        S3ImageUrlStringValidator.class,
        S3ImageUrlListValidator.class
})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface S3ImageUrl {

    String message() default "올바른 이미지 URL 형식이 아닙니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
