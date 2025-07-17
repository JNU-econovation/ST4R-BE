package star.home.board.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.CollectionUtils;
import star.common.dto.LocalDateTimesDTO;
import star.common.exception.ErrorCode;
import star.common.exception.server.InternalServerException;
import star.common.model.vo.CircularArea;
import star.common.model.vo.QJido;
import star.home.board.dto.BoardSearchDTO;
import star.home.board.model.entity.Board;
import star.home.board.model.entity.QBoard;
import star.home.category.model.vo.CategoryName;
import star.member.model.entity.QMember;

@Slf4j
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
                        .map(order -> getOrderSpecifier(order, board, distanceExpr))
                        .toArray(OrderSpecifier<?>[]::new))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    private NumberExpression<Double> buildSelectQuery(BooleanBuilder builder, QBoard board,
            BoardSearchDTO searchDTO) {

        LocalDateTimesDTO localDateTimesDTO = searchDTO.localDateTimesForSearch();
        List<CategoryName> categories = searchDTO.categories();
        CircularArea circularArea = searchDTO.circularArea();
        String title = searchDTO.title();
        String contentText = searchDTO.contentText();
        String authorName = searchDTO.authorName();

        buildCreatedAt(builder, board, localDateTimesDTO);

        buildCategory(builder, board, searchDTO);

        NumberExpression<Double> distanceExpr = buildDistance(builder, board, circularArea);

        buildTitleAndContent(builder, board, title, contentText);

        buildAuthorName(builder, board, authorName);

        return distanceExpr;
    }

    private static void buildCreatedAt(BooleanBuilder builder, QBoard board,
            LocalDateTimesDTO localDateTimesDTO) {
        builder.and(board.createdAt.between(localDateTimesDTO.start(), localDateTimesDTO.end()));
    }

    private static void buildCategory(BooleanBuilder builder, QBoard board,
            BoardSearchDTO searchDTO) {
        if (!CollectionUtils.isEmpty(searchDTO.categories())) {
            builder.and(board.category.name.in(searchDTO.categories()));
        }
    }

    private static NumberExpression<Double> buildDistance(BooleanBuilder builder,
            QBoard board, CircularArea circularArea) {
        NumberExpression<Double> distanceExpr = null;

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
        return distanceExpr;
    }

    private static void buildTitleAndContent(
            BooleanBuilder builder, QBoard board, String title, String contentText
    ) {
        boolean hasTitle = title != null;
        boolean hasContent = contentText != null;

        if (hasTitle && hasContent) {
            builder.and(
                    board.title.value.containsIgnoreCase(title)
                            .or(board.content.text.containsIgnoreCase(contentText))
            );
            return;
        }

        if (hasTitle) {
            builder.and(board.title.value.containsIgnoreCase(title));
        }

        if (hasContent) {
            builder.and(board.content.text.containsIgnoreCase(contentText));
        }
    }

    private static void buildAuthorName(BooleanBuilder builder, QBoard board, String authorName) {
        if (authorName != null) {
            builder.and(board.member.email.value.containsIgnoreCase(authorName.trim()));
        }
    }

    private static OrderSpecifier<?> getOrderSpecifier(Order order, QBoard board,
            NumberExpression<Double> distanceExpr) {
        boolean asc = order.isAscending();

        return switch (order.getProperty()) {
            case "createdAt" -> asc ? board.createdAt.asc() : board.createdAt.desc();
            case "viewCount" -> asc ? board.viewCount.asc() : board.viewCount.desc();
            case "heartCount" -> asc ? board.heartCount.asc() : board.heartCount.desc();
            case "distance" -> asc ? distanceExpr.asc() : distanceExpr.desc();
            default -> {
                log.error("알려지지 않은 정렬 프로퍼티 입니다. -> {} ", order.getProperty());
                throw new InternalServerException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        };
    }
}
