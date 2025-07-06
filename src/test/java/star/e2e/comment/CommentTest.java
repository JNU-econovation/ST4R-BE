package star.e2e.comment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static star.e2e.util.RestAssuredUtil.sendRequest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
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

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        MemberInfoDTO memberInfo = memberService.getMemberById(1L);
        accessToken = jwtManager.generateToken(memberInfo);
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

        for (int i = 0; i < 100; i++) {
            CommentRequest createCommentRequestBody = CommentRequest.builder()
                    .content("테스트 댓글 " + i)
                    .build();

            E2ERequest createRequest = E2ERequest.builder()
                    .url("/home/boards/1/comments")
                    .method(Method.POST)
                    .accessToken(accessToken)
                    .body(createCommentRequestBody)
                    .expectedStatusCode(201)
                    .build();

            sendRequest(createRequest);
        }

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


}