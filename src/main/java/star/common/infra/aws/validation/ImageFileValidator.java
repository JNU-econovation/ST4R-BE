package star.common.infra.aws.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import star.common.annotation.ImageFile;

public class ImageFileValidator implements ConstraintValidator<ImageFile, String> {
    private static final Set<String> ALLOWED_EXTENSIONS = Stream.of(
                    "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg"
            )
            .collect(Collectors.toUnmodifiableSet());

    @Override
    public boolean isValid(String fileName, ConstraintValidatorContext context) {
        if (fileName == null || fileName.isBlank()) {
            return false;
        }
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            return false; //디렉토리 탐색 공격 방어
        }
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex < 0 || lastIndex == fileName.length() - 1) {
            return false;
        }
        String extensions = fileName.substring(lastIndex + 1).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extensions);
    }
}
