package star.team.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import star.common.entity.BaseImageEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TeamImage extends BaseImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @Builder
    public TeamImage(Team team, String imageUrl, Integer sortOrder) {
        super(imageUrl, sortOrder);
        this.team = team;
    }
}
