package star.common.util;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import star.common.exception.client.InvalidPageableFieldException;
import star.common.constants.SortField;

public class CommonUtils {
    public static Pageable convertSortForDb(List<SortField> allowedSortFields, Pageable pageable) {

        validateSortFields(allowedSortFields, pageable);

        Sort convertedSort = Sort.by(
                pageable.getSort().stream()
                        .map(order -> {
                            SortField sortField = SortField.fromRequestKey(order.getProperty());
                            String dbField = (sortField != null) ? sortField.getDbField() : order.getProperty();
                            return new Sort.Order(order.getDirection(), dbField);
                        })
                        .toList()
        );
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), convertedSort);
    }

    private static void validateSortFields(List<SortField> allowedSortFields, Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            SortField sortField = SortField.fromRequestKey(order.getProperty());
            if (sortField == null || !allowedSortFields.contains(sortField)) {
                throw new InvalidPageableFieldException(order.getProperty());
            }
        }
    }

}
