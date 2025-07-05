package star.home.dto.request;


import java.util.List;
import lombok.Builder;
import org.springframework.lang.Nullable;
import star.common.dto.LocationParamsDTO;
import star.common.model.vo.CircularArea;
import star.home.category.model.vo.CategoryName;
import star.home.constants.Period;

@Builder
public record LobbyRequest(
        @Nullable Period period,
        List<CategoryName> categories,
        @Nullable String title,
        @Nullable String content,
        @Nullable String authorName,
        @Nullable CategoryName categoryName,
        @Nullable LocationParamsDTO location
) {
    public CircularArea circularArea() {
        return location != null ? location.toCircularArea() : null;
    }
}