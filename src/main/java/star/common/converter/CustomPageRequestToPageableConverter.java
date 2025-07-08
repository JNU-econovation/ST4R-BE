package star.common.converter;

import java.util.List;
import java.util.Optional;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import star.common.constants.SortField;
import star.common.dto.request.CustomPageRequest;
import star.common.exception.client.InvalidPageableFieldException;

public class CustomPageRequestToPageableConverter implements Converter<CustomPageRequest, Pageable> {

    private final List<SortField> allowedFields;

    public CustomPageRequestToPageableConverter(List<SortField> allowedFields) {
        this.allowedFields = allowedFields;
    }

    @Override
    public Pageable convert(CustomPageRequest request) {
        Integer page = Optional.ofNullable(request.page()).orElse(0);
        Integer size = Optional.ofNullable(request.size()).orElse(10);
        String directionString = Optional.ofNullable(request.direction()).orElse("asc");
        String requestFieldString = Optional.ofNullable(request.sort())
                .orElse(SortField.CREATED_AT.getRequestField());

        Direction direction;

        try {
            direction = Direction.fromString(directionString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidPageableFieldException(directionString);
        }

        boolean allowed = allowedFields.stream()
                .anyMatch(field -> field.getRequestField()
                        .equals(requestFieldString));

        if (!allowed) {
            throw new InvalidPageableFieldException(requestFieldString);
        }
        SortField sortField = SortField.fromRequestKey(requestFieldString);
        Order order = new Sort.Order(direction, sortField.getDbField());
        return PageRequest.of(page, size, Sort.by(order));
    }
}
