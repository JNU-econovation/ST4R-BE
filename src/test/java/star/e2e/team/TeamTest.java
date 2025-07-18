package star.e2e.team;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.notNullValue;
import static star.e2e.util.GeoGridUtil.generateRange;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import star.common.model.vo.Jido;
import star.common.model.vo.Marker;
import star.common.security.encryption.jwt.JwtManager;
import star.member.dto.MemberInfoDTO;
import star.member.service.MemberService;
import star.team.dto.request.CreateTeamRequest;
import star.team.dto.request.UpdateTeamRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // -> 테스트 클래스당 한 번만 인스턴스를 생성
public class TeamTest {

    private static final int COUNT = 100;
    private static final double MIN_LAT = -90;
    private static final double MAX_LAT = 90;
    private static final double MIN_LON = -180;
    private static final double MAX_LON = 180;

    @LocalServerPort
    private int port;

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtManager jwtManager;

    private String accessToken;
    private String memberName;

    @BeforeAll
    void setUp() {
        RestAssured.port = port;
        MemberInfoDTO memberInfo = memberService.getMemberById(1L);
        accessToken = jwtManager.generateToken(memberInfo);
        memberName = memberInfo.email().getValue();

        List<Double> latitudes = generateRange(MIN_LAT, MAX_LAT, COUNT);
        List<Double> longitudes = generateRange(MIN_LON, MAX_LON, COUNT);

        for (int i = 0; i < COUNT; i++) {
            Marker marker = new Marker(latitudes.get(i), longitudes.get(i), "테스트 주소 " + i,
                    "테스트 장소 " + i);
            Jido jido = new Jido(marker, 10);

            CreateTeamRequest createTeamRequestBody = CreateTeamRequest.builder()
                    .name("테스트 팀 " + i)
                    .description("테스트 팀 설명 " + i)
                    .whenToMeet(OffsetDateTime.now().plusDays(i + 1))
                    .location(jido)
                    .maxParticipantCount(10)
                    .password("1111")
                    .imageUrls(
                            List.of(
                                    "https://st4rbucket.s3.ap-northeast-2.amazonaws.com/asdf%d.jpeg"
                                            .formatted(i)
                            )
                    ).build();
            createTeamAndGetId(createTeamRequestBody);
        }
    }

    private Long createTeamAndGetId(CreateTeamRequest request) {
        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/groups");

        response.then()
                .statusCode(201)
                .header("Location", notNullValue());

        String location = response.getHeader("Location");
        return Long.valueOf(location.substring(location.lastIndexOf("/") + 1));
    }

    @Test
    @DisplayName("팀 생성 테스트")
    void createTeamTest() {
        Marker marker = new Marker(37.0, 127.0, "새로운 테스트 주소", "새로운 테스트 장소");
        Jido jido = new Jido(marker, 10);
        CreateTeamRequest createTeamRequestBody = CreateTeamRequest.builder()
                .name("새로운 테스트 팀")
                .description("새로운 테스트 팀 설명")
                .whenToMeet(OffsetDateTime.now().plusDays(1))
                .location(jido)
                .password("2222")
                .maxParticipantCount(10)
                .imageUrls(
                        List.of(
                                "https://st4rbucket.s3.ap-northeast-2.amazonaws.com/asdf.jpeg"
                        )
                )
                .build();
        createTeamAndGetId(createTeamRequestBody);
    }

    @Test
    @DisplayName("팀 목록 조회 테스트")
    void getTeamsTest() {
        int size = 20;
        int page = 0;

        given()
                .log().all()
                .queryParam("sort", "createdAt")
                .queryParam("direction", "desc")
                .queryParam("page", page)
                .queryParam("size", size)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/groups")
                .then()
                .log().all()
                .statusCode(200)
                .body("content.size()", equalTo(size));
    }

    @Test
    @DisplayName("팀 검색 테스트")
    void getTeamsSearchTest() {
        int size = 20;
        int page = 0;

        given()
                .log().all()
                .queryParam("name", "테스트 팀 1")
                .queryParam("leaderName", memberName)
                .queryParam("location.latitude", MIN_LAT)
                .queryParam("location.longitude", MIN_LON)
                .queryParam("location.distanceInMeters", Double.MAX_VALUE)
                .queryParam("location.roadAddress", "테스트")
                .queryParam("sort", "distance")
                .queryParam("direction", "asc")
                .queryParam("page", page)
                .queryParam("size", size)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/groups")
                .then()
                .log().all()
                .statusCode(200)
                .body("content.name", everyItem(containsString("테스트 팀 1")));
    }


    @Test
    @DisplayName("팀 상세 조회 테스트")
    void getTeamDetailsTest() {
        Marker marker = new Marker(38.0, 128.0, "상세 조회 주소", "상세 조회 장소");
        Jido jido = new Jido(marker, 5);
        CreateTeamRequest createTeamRequestBody = CreateTeamRequest.builder()
                .name("상세 조회 테스트 팀")
                .description("상세 조회 테스트 팀 설명")
                .whenToMeet(OffsetDateTime.now().plusDays(1))
                .location(jido)
                .maxParticipantCount(5)
                .imageUrls(
                        List.of(
                                "https://st4rbucket.s3.ap-northeast-2.amazonaws.com/asdf.jpeg"
                        )
                )
                .build();
        Long teamId = createTeamAndGetId(createTeamRequestBody);

        given()
                .log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/groups/" + teamId)
                .then()
                .log().all()
                .statusCode(200)
                .body("name", equalTo("상세 조회 테스트 팀"));
    }

    @Test
    @DisplayName("팀 수정 테스트")
    void updateTeamTest() {
        Marker markerBefore = new Marker(39.0, 129.0, "수정 전 주소", "수정 전 장소");
        Jido jidoBefore = new Jido(markerBefore, 1);
        CreateTeamRequest createTeamRequestBody = CreateTeamRequest.builder()
                .name("수정 전 팀")
                .description("수정 전 팀 설명")
                .whenToMeet(OffsetDateTime.now().plusDays(2))
                .location(jidoBefore)
                .maxParticipantCount(8)
                .imageUrls(
                        List.of(
                                "https://st4rbucket.s3.ap-northeast-2.amazonaws.com/asdf.jpeg"
                        )
                ).build();

        Long teamId = createTeamAndGetId(createTeamRequestBody);

        Marker markerAfter = new Marker(37.0, 127.0, "테스트 주소 수정", "테스트 장소 수정");
        Jido jidoAfter = new Jido(markerAfter, 10);

        UpdateTeamRequest requestBody = UpdateTeamRequest.builder()
                .name("수정 후 팀")
                .description("테스트 팀 설명 수정")
                .newWhenToMeet(OffsetDateTime.now().plusDays(1))
                .location(jidoAfter)
                .maxParticipantCount(10)
                .imageUrls(
                        List.of(
                                "https://st4rbucket.s3.ap-northeast-2.amazonaws.com/asdf.jpeg"
                        )
                )
                .changeWhenToMeet(true)
                .changePassword(false)
                .build();

        given()
                .log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/groups/" + teamId)
                .then()
                .log().all()
                .statusCode(200);

        given()
                .log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/groups/" + teamId)
                .then()
                .log().all()
                .statusCode(200)
                .body("name", equalTo("수정 후 팀"));
    }

    @Test
    @DisplayName("팀 삭제 테스트")
    void deleteTeamTest() {
        Marker marker = new Marker(40.0, 130.0, "삭제할 팀 주소", "삭제할 팀 장소");
        Jido jido = new Jido(marker, 3);
        CreateTeamRequest createTeamRequestBody = CreateTeamRequest.builder()
                .name("삭제할 팀")
                .description("삭제할 팀 설명")
                .whenToMeet(OffsetDateTime.now().plusDays(3))
                .location(jido)
                .maxParticipantCount(3)
                .imageUrls(
                        List.of(
                                "https://st4rbucket.s3.ap-northeast-2.amazonaws.com/asdf.jpeg"
                        )
                )
                .build();

        Long teamId = createTeamAndGetId(createTeamRequestBody);

        given()
                .log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .delete("/groups/" + teamId)
                .then()
                .log().all()
                .statusCode(204);

        given()
                .log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/groups/" + teamId)
                .then()
                .log().all()
                .statusCode(404);
    }
}
