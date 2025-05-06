package star.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


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