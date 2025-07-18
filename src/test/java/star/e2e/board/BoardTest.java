package star.e2e.board;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
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
public class BoardTest {

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
    @DisplayName("게시글 생성 테스트")
    void createBoardTest() {
        BoardRequest requestBodyWithInvalidImageUrls = BoardRequest.builder()
                .title("행운의 편지")
                .imageUrls(
                        List.of(
                                "https://s3.amazonaws.com/ajjultv/image1.png",
                                "https://s3.amazonaws.com/ajjultv/image2.png",
                                "https://s3.amazonaws.com/ajjultv/image3.png"
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

        E2ERequest createRequest = E2ERequest.builder()
                .url("/home/boards")
                .method(Method.POST)
                .accessToken(accessToken)
                .body(requestBodyWithInvalidImageUrls)
                .expectedStatusCode(400)
                .build();

        sendRequest(createRequest);

        BoardRequest requestBodyWithValidImageUrls = BoardRequest.builder()
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

        createRequest = E2ERequest.builder()
                .url("/home/boards")
                .method(Method.POST)
                .accessToken(accessToken)
                .body(requestBodyWithValidImageUrls)
                .expectedStatusCode(201)
                .build();

        sendRequest(createRequest);
    }

    @Test
    @DisplayName("게시글 조회 테스트")
    void getBoardTest() {
        E2ERequest request = E2ERequest.builder()
                .url("/home/boards/1")
                .method(Method.GET)
                .accessToken(accessToken)
                .expectedStatusCode(200)
                .build();

        sendRequest(request)
                .body("title", equalTo("test"));
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    void updateBoardTest() {

        //생성
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

        String location = response.getHeader("Location");

        //수정
        BoardRequest updateRequestBody = BoardRequest.builder()
                .title("행운의 편지2")
                .imageUrls(
                        List.of(
                                "https://st4rbucket.s3.ap-northeast-2.amazonaws.com/asdf1.jpeg",
                                "https://st4rbucket.s3.ap-northeast-2.amazonaws.com/asdf2.jpeg",
                                "https://st4rbucket.s3.ap-northeast-2.amazonaws.com/asdf3.jpeg"
                        )
                )
                .content(
                        Content.builder()
                                .text("이 게시글은 영국에서 시작됨 수정번")
                                .map(
                                        Jido.builder()
                                                .marker(
                                                        Marker.builder()
                                                                .latitude(35.123)
                                                                .longitude(121.456)
                                                                .locationName("location name test2")
                                                                .roadAddress("road address test2")
                                                                .build()
                                                ).zoomLevel(13)
                                                .build()
                                )
                                .build())
                .category("general")
                .build();

        E2ERequest updateRequest = E2ERequest.builder()
                .url(location)
                .method(Method.PUT)
                .accessToken(accessToken)
                .body(updateRequestBody)
                .expectedStatusCode(200)
                .build();

        sendRequest(updateRequest);

        //조회

        E2ERequest request = E2ERequest.builder()
                .url(location)
                .method(Method.GET)
                .accessToken(accessToken)
                .expectedStatusCode(200)
                .build();

        sendRequest(request)
                .body("title", equalTo("행운의 편지2"));

    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void deleteBoardTest() {
        //생성
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

        String location = response.getHeader("Location");

        //삭제
        E2ERequest deleteRequest = E2ERequest.builder()
                .url(location)
                .method(Method.DELETE)
                .accessToken(accessToken)
                .expectedStatusCode(204)
                .build();

        sendRequest(deleteRequest);

        //유효하지 않는 게시글 삭제
        E2ERequest deleteRequestExpect4XX = E2ERequest.builder()
                .url("/home/boards/-1")
                .method(Method.DELETE)
                .accessToken(accessToken)
                .expectedStatusCode(404)
                .build();

        sendRequest(deleteRequestExpect4XX);

    }

}