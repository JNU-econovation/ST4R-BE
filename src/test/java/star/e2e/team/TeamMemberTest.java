package star.e2e.team;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

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
import star.team.dto.request.TeamLeaderDelegateRequest;
import star.team.dto.request.TeamMemberUnbanRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TeamMemberTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtManager jwtManager;

    private String leaderAccessToken;
    private String memberAccessToken;
    private Long teamId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        MemberInfoDTO leaderInfo = memberService.getMemberById(1L);
        MemberInfoDTO memberInfo = memberService.getMemberById(2L);

        leaderAccessToken = jwtManager.generateToken(leaderInfo);
        memberAccessToken = jwtManager.generateToken(memberInfo);

        Marker marker = new Marker(37.0, 127.0, "테스트 주소", "테스트 장소");
        Jido jido = new Jido(marker, 10);
        CreateTeamRequest createTeamRequestBody = CreateTeamRequest.builder()
                .name("멤버 테스트 팀")
                .description("멤버 테스트 팀 설명")
                .whenToMeet(OffsetDateTime.now().plusDays(1))
                .location(jido)
                .maxParticipantCount(5)
                .imageUrls(List.of(
                        "https://st4rbucket.s3.ap-northeast-2.amazonaws.com/asdf1.jpeg")
                )
                .build();
        teamId = createTeamAndGetId(createTeamRequestBody);
    }

    private Long createTeamAndGetId(CreateTeamRequest request) {
        Response response = given()
                .header("Authorization", "Bearer " + leaderAccessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/groups");

        response.then().statusCode(201);

        String location = response.getHeader("Location");
        return Long.valueOf(location.substring(location.lastIndexOf("/") + 1));
    }

    @Test
    @DisplayName("팀 멤버 참여 및 탈퇴 테스트")
    void joinAndLeaveTeamTest() {
        // 팀 참여
        given()
                .log().all()
                .header("Authorization", "Bearer " + memberAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .post("/groups/" + teamId + "/members")
                .then()
                .log().all()
                .statusCode(204);

        given()
                .log().all()
                .header("Authorization", "Bearer " + leaderAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/groups/" + teamId)
                .then()
                .log().all()
                .statusCode(200)
                .body("nowParticipants", equalTo(2));

        // 팀 탈퇴
        given()
                .log().all()
                .header("Authorization", "Bearer " + memberAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .delete("/groups/" + teamId + "/members")
                .then()
                .log().all()
                .statusCode(204);

        given()
                .log().all()
                .header("Authorization", "Bearer " + leaderAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/groups/" + teamId)
                .then()
                .log().all()
                .statusCode(200)
                .body("nowParticipants", equalTo(1));
    }

    @Test
    @DisplayName("팀 멤버 목록 조회 테스트")
    void getTeamMembersTest() {
        // 멤버 추가
        given()
                .header("Authorization", "Bearer " + memberAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .post("/groups/" + teamId + "/members")
                .then()
                .statusCode(204);

        // 멤버 목록 조회
        given()
                .log().all()
                .header("Authorization", "Bearer " + leaderAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/groups/" + teamId + "/members")
                .then()
                .log().all()
                .statusCode(200)
                .body("teamMembers", hasSize(2));

        // 멤버 목록 조회 (토큰 없이)
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/groups/" + teamId + "/members")
                .then()
                .log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("모임장 위임 테스트")
    void delegateTeamLeaderTest() {
        // given
        // 멤버 추가
        given()
                .header("Authorization", "Bearer " + memberAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .post("/groups/" + teamId + "/members")
                .then()
                .statusCode(204);

        // when
        // 모임장 위임
        given()
                .log().all()
                .header("Authorization", "Bearer " + leaderAccessToken)
                .contentType(ContentType.JSON)
                .body(
                        TeamLeaderDelegateRequest.builder()
                                .targetMemberId(2L)
                                .build()
                )
                .when()
                .patch("/groups/" + teamId + "/members/leader")
                .then()
                .log().all()
                .statusCode(204);

        // then
        // 모임장 정보 확인
        given()
                .log().all()
                .header("Authorization", "Bearer " + memberAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/groups/" + teamId)
                .then()
                .log().all()
                .statusCode(200)
                .body("author.id", equalTo(2));

        // 이전 모임장이 위임 시도
        given()
                .log().all()
                .header("Authorization", "Bearer " + leaderAccessToken)
                .contentType(ContentType.JSON)
                .body(
                        TeamLeaderDelegateRequest.builder()
                                .targetMemberId(1L)
                                .build()
                )
                .when()
                .patch("/groups/" + teamId + "/members/leader")
                .then()
                .log().all()
                .statusCode(403);
    }

    @Test
    @DisplayName("팀 멤버 강퇴 및 강퇴 해제 테스트")
    void banAndUnbanTeamMemberTest() {
        // given
        // 멤버 추가
        given()
                .header("Authorization", "Bearer " + memberAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .post("/groups/" + teamId + "/members")
                .then()
                .statusCode(204);

        // when
        // 멤버 강퇴
        given()
                .log().all()
                .header("Authorization", "Bearer " + leaderAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .delete("/groups/" + teamId + "/members/2")
                .then()
                .log().all()
                .statusCode(204);

        // then
        // 강퇴 후 멤버 목록 확인
        given()
                .log().all()
                .header("Authorization", "Bearer " + leaderAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/groups/" + teamId + "/members")
                .then()
                .log().all()
                .statusCode(200)
                .body("teamMembers", hasSize(1));

        // 강퇴된 멤버 목록 확인
        given()
                .log().all()
                .header("Authorization", "Bearer " + leaderAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/groups/" + teamId + "/members/bannedMembers")
                .then()
                .log().all()
                .statusCode(200)
                .body("", hasSize(1));

        // 강퇴된 멤버가 재 참여 시도
        given()
                .log().all()
                .header("Authorization", "Bearer " + memberAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .post("/groups/" + teamId + "/members")
                .then()
                .log().all()
                .statusCode(403);

        // 리더가 자신을 강퇴 시도
        given()
                .log().all()
                .header("Authorization", "Bearer " + leaderAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .delete("/groups/" + teamId + "/members/1")
                .then().log().all()
                .statusCode(400);

        // when
        // 강퇴 해제
        given()
                .log().all()
                .header("Authorization", "Bearer " + leaderAccessToken)
                .contentType(ContentType.JSON)
                .body(new TeamMemberUnbanRequest(2L))
                .when()
                .patch("/groups/" + teamId + "/members/bannedMembers")
                .then()
                .log().all()
                .statusCode(204);

        // then
        // 강퇴 해제 후 강퇴된 멤버 목록 확인

        given()
                .log().all()
                .header("Authorization", "Bearer " + leaderAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/groups/" + teamId + "/members/bannedMembers")
                .then()
                .log().all()
                .statusCode(200)
                .body("", hasSize(0));

        // 강퇴 해제된 멤버가 재 참여 시도
        given()
                .log().all()
                .header("Authorization", "Bearer " + memberAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .post("/groups/" + teamId + "/members")
                .then()
                .log().all()
                .statusCode(204);

        // 재 참여 후 멤버 목록 확인
        given()
                .log().all()
                .header("Authorization", "Bearer " + leaderAccessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/groups/" + teamId + "/members")
                .then()
                .log().all()
                .statusCode(200)
                .body("teamMembers", hasSize(2));
    }
}
