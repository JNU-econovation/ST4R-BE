package star.home.board.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import star.common.dto.LocalDateTimesDTO;
import star.common.exception.server.InternalServerException;
import star.common.model.vo.CircularArea;
import star.common.model.vo.QJido;
import star.home.board.dto.BoardSearchDTO;
import star.home.board.model.entity.Board;
import star.home.board.model.entity.QBoard;
import star.home.board.model.vo.Content;
import star.home.board.model.vo.Title;
import star.home.category.model.vo.CategoryName;
import star.member.model.entity.QMember;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Board> searchBoards(BoardSearchDTO searchDTO, Pageable pageable) {

        QBoard board = QBoard.board;
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();
        NumberExpression<Double> distanceExpr = buildSelectQuery(builder, board, searchDTO);

        JPAQuery<Board> selectQuery = queryFactory
                .selectFrom(board)
                .leftJoin(board.member, member)
                .fetchJoin()
                .where(builder);

        JPAQuery<Long> countQuery = queryFactory
                .select(board.count())
                .from(board)
                .leftJoin(board.member, member)
                .where(builder);

        Long totalCount = countQuery.fetchOne();
        long total = (totalCount != null ? totalCount : 0L);

        List<Board> results = selectQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(pageable.getSort().stream()
                        .map(order -> {
                            boolean asc = order.isAscending();
                            OrderSpecifier<?> spec;
                            switch (order.getProperty()) {
                                case "createdAt":
                                    spec = asc ? board.createdAt.asc() : board.createdAt.desc();
                                    break;
                                case "viewCount":
                                    spec = asc ? board.viewCount.asc() : board.viewCount.desc();
                                    break;
                                case "heartCount":
                                    spec = asc ? board.heartCount.asc() : board.heartCount.desc();
                                    break;
                                case "distance":
                                    if (distanceExpr == null) {
                                        throw new InternalServerException(
                                                "SortField가 거리 순이지만 distanceExpr에 거리가 없음"
                                        );
                                    }
                                    spec = asc ? distanceExpr.asc() : distanceExpr.desc();
                                    break;
                                default:
                                    throw new IllegalArgumentException(
                                            "알려지지 않은 정렬 프로퍼티 입니다. -> " + order.getProperty()
                                    );
                            }
                            return spec;
                        }).toArray(OrderSpecifier<?>[]::new))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    private NumberExpression<Double> buildSelectQuery(BooleanBuilder builder, QBoard board,
            BoardSearchDTO searchDTO) {
        LocalDateTimesDTO localDateTimesDTO = searchDTO.localDateTimesForSearch();
        List<CategoryName> categories = searchDTO.categories();
        CircularArea circularArea = searchDTO.circularArea();
        Title title = searchDTO.title();
        Content content = searchDTO.content();
        String authorName = searchDTO.authorName();

        NumberExpression<Double> distanceExpr = null;

        // 1) createdAt between start, end
        builder.and(board.createdAt.between(localDateTimesDTO.start(), localDateTimesDTO.end()));

        // 2) categoryName IN (...)
        if (!CollectionUtils.isEmpty(searchDTO.categories())) {
            builder.and(board.category.name.in(searchDTO.categories()));
        }

        if (circularArea != null) {
            Double latitude = circularArea.marker().getLatitude();
            Double longitude = circularArea.marker().getLongitude();
            Double distanceInMeters = circularArea.distanceInMeters();
            QJido jido = board.content.map;

            builder.and(jido.isNotNull());

            distanceExpr = Expressions.numberTemplate(
                    Double.class,
                    "6371000 * acos(least(1.0, cos(radians({0})) * cos(radians({1})) * " +
                            "cos(radians({2}) - radians({3})) + sin(radians({0})) * sin(radians({1}))))",
                    latitude, jido.marker.latitude, jido.marker.longitude, longitude
            );
            builder.and(distanceExpr.loe(distanceInMeters));

        }

        // 4) title contains
        if (title != null) {
            builder.and(board.title.value.containsIgnoreCase(title.value()));
        }

        // 5) content contains
        if (content != null) {
            builder.and(board.content.text.containsIgnoreCase(content.getText()));
        }

        // 6) authorName contains
        if (authorName != null) {
            builder.and(board.member.email.value.containsIgnoreCase(authorName.trim()));
        }

        return distanceExpr;
    }
}
