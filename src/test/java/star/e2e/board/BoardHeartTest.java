package star.e2e.board;

import static io.restassured.RestAssured.given;
import static star.e2e.util.RestAssuredUtil.sendRequest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
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
import star.e2e.dto.request.E2ERequest;
import star.home.board.dto.request.BoardRequest;
import star.home.board.model.vo.Content;
import star.member.dto.MemberInfoDTO;
import star.member.service.MemberService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BoardHeartTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtManager jwtManager;

    private String accessToken;

    private String location;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        MemberInfoDTO memberInfo = memberService.getMemberById(1L);
        accessToken = jwtManager.generateToken(memberInfo);

        BoardRequest createRequestBody = BoardRequest.builder()
                .title("행운의 편지")
                .imageUrls(
                        List.of(
                                "https://st4rbucket.s3.ap-northeast-2.amazonaws.com/asdf1.jpeg",
                                "https://st4rbucket.s3.ap-northeast-2.amazonaws.com/asdf2.jpeg",
                                "https://st4rbucket.s3.ap-northeast-2.amazonaws.com/asdf3.jpeg"
                        )
                )
                .content(
                        Content.builder()
                                .text("이 게시글은 영국에서 시작됨 ㅅㄱ")
                                .map(
                                        Jido.builder()
                                                .marker(
                                                        Marker.builder()
                                                                .latitude(37.123)
                                                                .longitude(127.456)
                                                                .locationName("location name test")
                                                                .roadAddress("road address test")
                                                                .build()
                                                ).zoomLevel(13)
                                                .build()
                                )
                                .build())
                .category("spot")
                .build();

        Response response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(createRequestBody)
                .when()
                .post("/home/boards");

        location = response.getHeader("Location");
    }

    @Test
    @DisplayName("게시글 좋아요 테스트")
    void createBoardHeartTest() {

        E2ERequest createRequest = E2ERequest.builder()
                .url(location + "/likes")
                .method(Method.POST)
                .accessToken(accessToken)
                .expectedStatusCode(204)
                .build();

        sendRequest(createRequest);

        E2ERequest createDuplicatedHeartRequest = E2ERequest.builder()
                .url(location + "/likes")
                .method(Method.POST)
                .accessToken(accessToken)
                .expectedStatusCode(409)
                .build();

        sendRequest(createDuplicatedHeartRequest);
    }

    @Test
    @DisplayName("게시글 좋아요 취소 테스트")
    void deleteBoardHeartTest() {

        E2ERequest createRequest = E2ERequest.builder()
                .url(location + "/likes")
                .method(Method.POST)
                .accessToken(accessToken)
                .expectedStatusCode(204)
                .build();

        sendRequest(createRequest);

        E2ERequest deleteRequest = E2ERequest.builder()
                .url(location + "/likes")
                .method(Method.DELETE)
                .accessToken(accessToken)
                .expectedStatusCode(204)
                .build();

        sendRequest(deleteRequest);
    }

}