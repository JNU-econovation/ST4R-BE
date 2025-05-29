package star.home.category.service;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import star.home.category.exception.NoSuchCategoryException;
import star.home.category.model.entity.Category;
import star.home.category.model.vo.CategoryName;
import star.home.category.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category getCategory(String name) {
        CategoryName categoryName;

        try {
            categoryName = CategoryName.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new NoSuchCategoryException(name);
        }

        if (!categoryRepository.existsByName(categoryName))
            throw new NoSuchCategoryException(name);

        return categoryRepository.getCategoryByName(categoryName);
    }
}
