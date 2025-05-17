package star.common.infra.aws.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ImageFileValidator.class)
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface ImageFile {
    String message() default "파일 확장자는 jpg, jpeg, png, gif, bmp, webp, svg 만 가능합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}