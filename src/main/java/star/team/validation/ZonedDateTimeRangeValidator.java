package star.team.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.OffsetDateTime;
import star.team.annotation.ValidZonedDateTimeRange;
import star.team.dto.request.GetTeamsRequest;

public class ZonedDateTimeRangeValidator implements
        ConstraintValidator<ValidZonedDateTimeRange, GetTeamsRequest> {

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
