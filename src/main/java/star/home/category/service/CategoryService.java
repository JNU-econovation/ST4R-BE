package star.home.category.service;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import star.home.category.exception.NoSuchCategoryException;
import star.home.category.model.entity.Category;
import star.home.category.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category getCategory(String name) {
        if (!categoryRepository.existsByName(name.toUpperCase(Locale.ROOT)))
            throw new NoSuchCategoryException(name);

        return categoryRepository.getCategoryByName(name);
    }
}
