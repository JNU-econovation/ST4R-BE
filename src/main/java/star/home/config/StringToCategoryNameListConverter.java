package star.home.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;
import star.home.category.model.vo.CategoryName;
import org.springframework.core.convert.converter.Converter;

@Component
public class StringToCategoryNameListConverter implements Converter<String, List<CategoryName>> {

    @Override
    public List<CategoryName> convert(String source) {
        return Arrays.stream(source.split(","))
                .map(String::trim)
                .map(CategoryName::from)
                .toList();
    }
}
