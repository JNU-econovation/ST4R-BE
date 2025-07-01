package star.home.board.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import star.home.board.model.entity.Board;
import star.home.category.model.vo.CategoryName;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Optional<Board> getBoardById(Long id);

    Page<Board> getBoardsByCategoryNameInAndCreatedAtBetween(
            List<CategoryName> categoryNames,
            LocalDateTime start, LocalDateTime end,
            Pageable pageable
    );


    @Query(value = """
        SELECT b.*
        FROM board b
        WHERE b.category_id IN (
            SELECT c.id FROM category c WHERE c.name IN (:categoryNames)
        )
        AND b.created_at BETWEEN :start AND :end
        AND b.latitude IS NOT NULL
        AND b.longitude IS NOT NULL
        AND (
            6371000 * acos(LEAST(1.0,
                cos(radians(:latitude)) * cos(radians(b.latitude)) *
                cos(radians(b.longitude) - radians(:longitude)) +
                sin(radians(:latitude)) * sin(radians(b.latitude))
            ))
        ) <= :distanceInMeters
        """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM board b
                    WHERE b.category_id IN (
                        SELECT c.id FROM category c WHERE c.name IN (:categoryNames)
                    )
                    AND b.created_at BETWEEN :start AND :end
                    AND b.latitude IS NOT NULL
                    AND b.longitude IS NOT NULL
                    AND (
                        6371000 * acos(LEAST(1.0,
                            cos(radians(:latitude)) * cos(radians(b.latitude)) *
                            cos(radians(b.longitude) - radians(:longitude)) +
                            sin(radians(:latitude)) * sin(radians(b.latitude))
                        ))
                    ) <= :distanceInMeters
                """,
            nativeQuery = true)
    Page<Board> getBoardsByCategoryAndCreatedAtAndNearbyCircularArea(
            @Param("categoryNames") List<String> categoryNames,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distanceInMeters") double distanceInMeters,
            Pageable pageable
    );

    @Query(value = """
        SELECT b.*
        FROM board b
        WHERE b.category_id IN (
            SELECT c.id FROM category c WHERE c.name IN (:categoryNames)
        )
        AND b.created_at BETWEEN :start AND :end
        AND b.latitude IS NOT NULL
        AND b.longitude IS NOT NULL
        AND (
            6371000 * acos(LEAST(1.0,
                cos(radians(:latitude)) * cos(radians(b.latitude)) *
                cos(radians(b.longitude) - radians(:longitude)) +
                sin(radians(:latitude)) * sin(radians(b.latitude))
            ))
        ) <= :distanceInMeters
        ORDER BY (
            6371000 * acos(LEAST(1.0,
                cos(radians(:latitude)) * cos(radians(b.latitude)) *
                cos(radians(b.longitude) - radians(:longitude)) +
                sin(radians(:latitude)) * sin(radians(b.latitude))
            ))
        ) ASC
        """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM board b
                    WHERE b.category_id IN (
                        SELECT c.id FROM category c WHERE c.name IN (:categoryNames)
                    )
                    AND b.created_at BETWEEN :start AND :end
                    AND b.latitude IS NOT NULL
                    AND b.longitude IS NOT NULL
                    AND (
                        6371000 * acos(LEAST(1.0,
                            cos(radians(:latitude)) * cos(radians(b.latitude)) *
                            cos(radians(b.longitude) - radians(:longitude)) +
                            sin(radians(:latitude)) * sin(radians(b.latitude))
                        ))
                    ) <= :distanceInMeters
                """,
            nativeQuery = true)
    Page<Board> getBoardsByCategoryAndCreatedAtAndNearbyCircularAreaOrderByDistanceAsc(
            @Param("categoryNames") List<String> categoryNames,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distanceInMeters") double distanceInMeters,
            Pageable pageable
    );

    @Query(value = """
        SELECT b.*
        FROM board b
        WHERE b.category_id IN (
            SELECT c.id FROM category c WHERE c.name IN (:categoryNames)
        )
        AND b.created_at BETWEEN :start AND :end
        AND b.latitude IS NOT NULL
        AND b.longitude IS NOT NULL
        AND (
            6371000 * acos(LEAST(1.0,
                cos(radians(:latitude)) * cos(radians(b.latitude)) *
                cos(radians(b.longitude) - radians(:longitude)) +
                sin(radians(:latitude)) * sin(radians(b.latitude))
            ))
        ) <= :distanceInMeters
        ORDER BY (
            6371000 * acos(LEAST(1.0,
                cos(radians(:latitude)) * cos(radians(b.latitude)) *
                cos(radians(b.longitude) - radians(:longitude)) +
                sin(radians(:latitude)) * sin(radians(b.latitude))
            ))
        ) DESC
        """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM board b
                    WHERE b.category_id IN (
                        SELECT c.id FROM category c WHERE c.name IN (:categoryNames)
                    )
                    AND b.created_at BETWEEN :start AND :end
                    AND b.latitude IS NOT NULL
                    AND b.longitude IS NOT NULL
                    AND (
                        6371000 * acos(LEAST(1.0,
                            cos(radians(:latitude)) * cos(radians(b.latitude)) *
                            cos(radians(b.longitude) - radians(:longitude)) +
                            sin(radians(:latitude)) * sin(radians(b.latitude))
                        ))
                    ) <= :distanceInMeters
                """,
            nativeQuery = true)
    Page<Board> getBoardsByCategoryAndCreatedAtAndNearbyCircularAreaOrderByDistanceDesc(
            @Param("categoryNames") List<String> categoryNames,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distanceInMeters") double distanceInMeters,
            Pageable pageable
    );

}