package star.fortune.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import star.common.model.entity.BaseEntity;
import star.member.constants.Constellation;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fortune extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Constellation constellation;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String fortune;

    @Column(nullable = false)
    private LocalDate date;

    @Builder
    public Fortune(Constellation constellation, String fortune, LocalDate date) {
        this.constellation = constellation;
        this.fortune = fortune;
        this.date = date;
    }

}
