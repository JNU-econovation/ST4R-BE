package star.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import star.home.category.model.vo.CategoryName;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardSearchParamsDTO {
    private String title;
    private String content;
    private CategoryName categoryName;
    private String locationName;
}
