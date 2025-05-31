package star.common.model.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;


@Getter
@MappedSuperclass
public abstract class SoftDeletableEntity extends BaseEntity {

    private boolean isDeprecated;

    public void markAsDeprecated() {
        this.isDeprecated = true;
    }

    public void markAsActivated() {
        this.isDeprecated = false;
    }

}