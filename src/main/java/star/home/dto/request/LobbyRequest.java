package star.home.dto.request;


import java.util.List;
import org.springframework.lang.Nullable;
import star.common.dto.LocationParamsDTO;
import star.common.model.vo.CircularArea;
import star.home.category.model.vo.CategoryName;
import star.home.constants.Period;


public record LobbyRequest(
        @Nullable Period period,
        List<CategoryName> categories,
        @Nullable LocationParamsDTO location
) {
    public CircularArea circularArea() {
        return location != null ? location.toCircularArea() : null;
    }
}