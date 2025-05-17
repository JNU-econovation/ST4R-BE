package star.home.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import star.home.category.model.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    Category getCategoryByName(String name);

    Boolean existsByName(String name);
}
