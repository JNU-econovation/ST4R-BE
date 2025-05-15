package star.home.category.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import star.common.entity.BaseEntity;

@Entity
@Getter
public class Category extends BaseEntity {
    @Id
    @Column(nullable = false, length = 20, unique = true)
    private String name;
}
