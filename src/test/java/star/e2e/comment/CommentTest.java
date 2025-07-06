package star.e2e.comment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static star.e2e.util.RestAssuredUtil.sendRequest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import star.common.security.encryption.jwt.JwtManager;
import star.e2e.dto.request.E2ERequest;
import star.home.comment.dto.request.CommentRequest;
import star.member.dto.MemberInfoDTO;
import star.member.service.MemberService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtManager jwtManager;

    private String accessToken;

    private String recentLocation;

    private Long recentCommentId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        MemberInfoDTO memberInfo = memberService.getMemberById(1L);
        accessToken = jwtManager.generateToken(memberInfo);

        for (int i = 0; i < 100; i++) {
            CommentRequest createCommentRequestBody = CommentRequest.builder()
                    .content("테스트 댓글 " + i)
                    .build();

            Response response = given()
                    .log().all()
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(ContentType.JSON)
                    .body(createCommentRequestBody)
                    .when()
                    .post("/home/boards/1/comments");

            if (i == 99) {
                recentLocation = response.getHeader("Location");
                recentCommentId = Long.valueOf(
                        recentLocation.substring(recentLocation.lastIndexOf("/") + 1));
            }

            response.then()
                    .log().all()
                    .statusCode(201);
        }
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    void createCommentTest() {
        CommentRequest requestBody = CommentRequest.builder()
                .content("테스트 댓글")
                .build();

        E2ERequest createRequest = E2ERequest.builder()
                .url("/home/boards/1/comments")
                .method(Method.POST)
                .accessToken(accessToken)
                .body(requestBody)
                .expectedStatusCode(201)
                .build();

        sendRequest(createRequest);
    }


    @Test
    @DisplayName("댓글 조회 테스트")
    void getCommentTest() {
        int size = 10;
        int page = 0;

        given()
                .log().all()
                .queryParam("sort", "createdAt")
                .queryParam("order", "desc")
                .queryParam("page", page)
                .queryParam("size", size)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/home/boards/1/comments")
                .then()
                .log().all()
                .statusCode(200)
                .body("content.size()", equalTo(size));
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void updateCommentTest() {
        CommentRequest requestBody = CommentRequest.builder()
                .content("테스트 댓글 수정")
                .build();

        given()
                .log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/home/boards/1/comments/" + recentCommentId)
                .then()
                .log().all()
                .statusCode(200);

        int page = 0;
        int size = 10;

        given()
                .log().all()
                .queryParam("sort", "createdAt")
                .queryParam("order", "desc")
                .queryParam("page", page)
                .queryParam("size", size)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/home/boards/1/comments")
                .then()
                .log().all()
                .statusCode(200)
                .body("content.size()", equalTo(size))
                .body("content.getFirst().content", equalTo("테스트 댓글 수정"));


    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void deleteCommentTest() {
        given()
                .log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .delete("/home/boards/1/comments/" + recentCommentId)
                .then()
                .log().all()
                .statusCode(204);

        int page = 0;
        int size = 10;

        given()
                .log().all()
                .queryParam("sort", "createdAt")
                .queryParam("order", "desc")
                .queryParam("page", page)
                .queryParam("size", size)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/home/boards/1/comments")
                .then()
                .log().all()
                .statusCode(200)
                .body("content.size()", equalTo(size))
                .body("content.getFirst().id", not(equalTo(recentCommentId)));
    }

}