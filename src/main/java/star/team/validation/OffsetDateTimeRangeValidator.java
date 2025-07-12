package star.team.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.OffsetDateTime;
import star.team.annotation.ValidOffsetDateTimeRange;
import star.team.dto.request.GetTeamsRequest;

public class OffsetDateTimeRangeValidator implements
        ConstraintValidator<ValidOffsetDateTimeRange, GetTeamsRequest> {

    @Override
    public boolean isValid(GetTeamsRequest request, ConstraintValidatorContext context) {
        OffsetDateTime start = request.meetBetweenStart();
        OffsetDateTime end = request.meetBetweenEnd();

        if (start == null || end == null) {
            return true;
        }

        return start.isBefore(end);
    }
}
