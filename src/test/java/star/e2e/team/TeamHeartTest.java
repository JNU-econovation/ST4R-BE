package star.e2e.team;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import star.common.model.vo.Jido;
import star.common.model.vo.Marker;
import star.common.security.encryption.jwt.JwtManager;
import star.member.dto.MemberInfoDTO;
import star.member.service.MemberService;
import star.team.dto.request.CreateTeamRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TeamHeartTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtManager jwtManager;

    private String accessToken;
    private Long teamId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        MemberInfoDTO memberInfo = memberService.getMemberById(1L);
        accessToken = jwtManager.generateToken(memberInfo);

        Marker marker = new Marker(37.0, 127.0, "테스트 주소", "테스트 장소");
        Jido jido = new Jido(marker, 10);
        CreateTeamRequest createTeamRequestBody = CreateTeamRequest.builder()
                .name("하트 테스트 팀")
                .description("하트 테스트 팀 설명")
                .whenToMeet(OffsetDateTime.now().plusDays(1))
                .location(jido)
                .maxParticipantCount(10)
                .imageUrls(List.of("https://st4rbucket.s3.ap-northeast-2.amazonaws.com/asdf1.jpeg"))
                .build();
        teamId = createTeamAndGetId(createTeamRequestBody);
    }

    private Long createTeamAndGetId(CreateTeamRequest request) {
        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/groups");

        response.then().statusCode(201);

        String location = response.getHeader("Location");
        return Long.valueOf(location.substring(location.lastIndexOf("/") + 1));
    }

    @Test
    @DisplayName("팀 좋아요 및 좋아요 취소 테스트")
    void toggleTeamHeartTest() {
        // 좋아요
        given()
                .log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .post("/groups/" + teamId + "/likes")
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
                .statusCode(200)
                .body("likeCount", equalTo(1))
                .body("liked", equalTo(true));

        // 좋아요 취소
        given()
                .log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .delete("/groups/" + teamId + "/likes")
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
                .statusCode(200)
                .body("likeCount", equalTo(0))
                .body("liked", equalTo(false));
    }
}
