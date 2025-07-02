package star.team.repository;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import star.common.dto.LocalDateTimesDTO;
import star.common.exception.client.IncompatibleRequestParametersException;
import star.common.exception.server.InternalServerException;
import star.common.model.vo.CircularArea;
import star.common.model.vo.QJido;
import star.member.model.entity.QMember;
import star.team.dto.TeamSearchDTO;
import star.team.model.entity.QTeam;
import star.team.model.entity.Team;
import star.team.model.vo.Name;

@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public Page<Team> searchTeams(TeamSearchDTO searchDTO, Pageable pageable) {

        QTeam team = QTeam.team;
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();
        NumberExpression<Double> distanceExpr = buildSelectTeamQuery(builder, team, searchDTO);

        JPAQuery<Team> selectQuery = queryFactory.selectFrom(team)
                .leftJoin(team.leader, member)
                .fetchJoin()
                .where(builder);

        JPAQuery<Long> countQuery = queryFactory
                .select(team.count())
                .from(team)
                .leftJoin(team.leader, member)
                .where(builder);

        Long totalCount = countQuery.fetchOne();
        long total = (totalCount != null ? totalCount : 0L);

        List<Team> results = selectQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(pageable.getSort().stream()
                        .map(order -> {
                            boolean asc = order.isAscending();
                            OrderSpecifier<?> spec = switch (order.getProperty()) {
                                case "createdAt" ->
                                        asc ? team.createdAt.asc() : team.createdAt.desc();
                                case "whenToMeet" ->
                                        asc ? team.whenToMeet.asc() : team.whenToMeet.desc();
                                case "heartCount" ->
                                        asc ? team.heartCount.asc() : team.heartCount.desc();
                                case "distance" -> {
                                    if (distanceExpr == null) {
                                        throw new InternalServerException(
                                                "SortField가 거리 순이지만 distanceExpr에 거리가 없음"
                                        );
                                    }
                                    yield asc ? distanceExpr.asc() : distanceExpr.desc();
                                }
                                default -> throw new IllegalArgumentException(
                                        "알려지지 않은 정렬 프로퍼티 입니다. -> " + order.getProperty()
                                );
                            };
                            return spec;
                        }).toArray(OrderSpecifier<?>[]::new))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    private NumberExpression<Double> buildSelectTeamQuery(BooleanBuilder builder, QTeam team,
            TeamSearchDTO searchDTO) {
        LocalDateTimesDTO meetBetween = searchDTO.meetBetween();
        CircularArea circularArea = searchDTO.circularArea();
        Name name = searchDTO.name();
        String leaderName = searchDTO.leaderName();
        boolean responseIncludePastMeetTime = searchDTO.includePast();

        NumberExpression<Double> distanceExpr = null;

        // 1) whenToMeet between start, end Or Past
        if (meetBetween != null) {
            if (responseIncludePastMeetTime) {
                throw new IncompatibleRequestParametersException("모임 날짜", "과거 모임 표시");
            }
            builder.and(team.whenToMeet.between(meetBetween.start(), meetBetween.end()));
        } else if (!responseIncludePastMeetTime) {
            builder.and(team.whenToMeet.after(LocalDateTime.now()));
        }

        //2
        if (circularArea != null) {
            Double latitude = circularArea.marker().getLatitude();
            Double longitude = circularArea.marker().getLongitude();
            Double distanceInMeters = circularArea.distanceInMeters();
            QJido jido = team.location;

            builder.and(jido.isNotNull());

            distanceExpr = Expressions.numberTemplate(
                    Double.class,
                    "6371000 * acos(least(1.0, cos(radians({0})) * cos(radians({1})) * " +
                            "cos(radians({2}) - radians({3})) + sin(radians({0})) * sin(radians({1}))))",
                    latitude, jido.marker.latitude, jido.marker.longitude, longitude
            );
            builder.and(distanceExpr.loe(distanceInMeters));

        }

        // 3) title contains
        if (name != null) {
            builder.and(team.name.value.containsIgnoreCase(name.getValue()));
        }

        // 4) authorName contains
        if (leaderName != null) {
            builder.and(team.leader.email.value.containsIgnoreCase(leaderName.trim()));
        }

        return distanceExpr;
    }


}
