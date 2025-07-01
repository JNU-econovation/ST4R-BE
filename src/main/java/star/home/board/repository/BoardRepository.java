package star.home.board.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.locationtech.jts.geom.Point;
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


    /**
     * 지정된 카테고리, 기간, 그리고 중심 좌표로부터 특정 거리 내의 게시글 검색
     *
     * @param categoryNames    검색할 카테고리 이름 목록
     * @param start            검색 시작 시간
     * @param end              검색 종료 시간
     * @param center           검색의 중심이 될 좌표
     * @param distanceInMeters 최대 검색 거리 (단위: 미터)
     * @param pageable         페이징
     * @return 페이징된 게시글 목록
     */
    @Query("SELECT b FROM Board b " +
            "WHERE b.category.name IN :categoryNames " +
            "  AND b.createdAt BETWEEN :start AND :end " +
            "  AND b.location IS NOT NULL " +
            "  AND distance(b.location, :center) <= :distanceInMeters")
    //distance : hibernate-spatial이 알아서 (dialect 보고) MySQL / H2 거리 함수로 바꾸는 함수

    Page<Board> getBoardsByCategoryNameInAndCreatedAtBetweenAndLocationNear(
            @Param("categoryNames") List<CategoryName> categoryNames,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("center") Point center,
            @Param("distanceInMeters") double distanceInMeters,
            Pageable pageable
    );
}