package star.home.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import star.home.category.model.entity.Category;
import star.home.category.model.vo.CategoryName;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    Category getCategoryByName(CategoryName name);

    Boolean existsByName(CategoryName name);
}
