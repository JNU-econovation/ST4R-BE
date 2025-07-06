package star.e2e.lobby;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static star.e2e.util.GeoGridUtil.generateRange;
import static star.e2e.util.RestAssuredUtil.sendRequest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import java.util.ArrayList;
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
import star.e2e.dto.request.E2ERequest;
import star.home.board.dto.request.BoardRequest;
import star.home.board.model.vo.Content;
import star.member.dto.MemberInfoDTO;
import star.member.service.MemberService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // -> 테스트 클래스당 한 번만 인스턴스를 생성
public class LobbyTest {

    private static final int COUNT = 1000;

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

    private List<String> locations = new ArrayList<>();

    @BeforeAll
    void setUp() {
        RestAssured.port = port;
        MemberInfoDTO memberInfo = memberService.getMemberById(1L);
        accessToken = jwtManager.generateToken(memberInfo);

        List<Double> latitudes = generateRange(MIN_LAT, MAX_LAT, COUNT);
        List<Double> longitudes = generateRange(MIN_LON, MAX_LON, COUNT);
        List<String> categories = List.of("spot", "general", "promotion");

        for (int i = 0; i < COUNT; i++) {

            BoardRequest createRequestBody = BoardRequest.builder()
                    .title("행운의 편지 " + i)
                    .imageUrls(
                            List.of(
                                    "https://s3.amazonaws.com/ajjultv/image1.png",
                                    "https://s3.amazonaws.com/ajjultv/image2.png",
                                    "https://s3.amazonaws.com/ajjultv/image3.png"
                            )
                    )
                    .content(
                            Content.builder()
                                    .text("이 게시글은 영국에서 시작됨" + i)
                                    .map(
                                            Jido.builder()
                                                    .marker(
                                                            Marker.builder()
                                                                    .longitude(longitudes.get(i))
                                                                    .latitude(latitudes.get(i))
                                                                    .locationName(
                                                                            "location name test"
                                                                                    + i)
                                                                    .roadAddress(
                                                                            "road address test" + i)
                                                                    .build()
                                                    ).zoomLevel(i % 13 + 1)
                                                    .build()
                                    )
                                    .build())
                    .category(categories.get(i % 3))
                    .build();

            Response response = given()
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(ContentType.JSON)
                    .body(createRequestBody)
                    .when()
                    .post("/home/boards");

            locations.add(response.getHeader("Location"));

        }


    }

    @Test
    @DisplayName("로비 단순 조회 테스트")
    void getLobbyTest() {
        E2ERequest request = E2ERequest.builder()
                .url("/home")
                .method(Method.GET)
                .accessToken(accessToken)
                .expectedStatusCode(200)
                .build();

        sendRequest(request);
    }

    @Test
    @DisplayName("로비 검색 테스트")
    void getLobbySearchTest() {

        int size = 20;
        int page = 0;

        given()
                .log().all()
                .queryParam("period", "daily")
                .queryParam("categories", "spot,general")
                .queryParam("location.latitude", 35.17295477055739)
                .queryParam("location.longitude", 126.90511701840629)
                .queryParam("location.distanceInMeters", Double.MAX_VALUE)
                .queryParam("location.roadAddress", "테스트")
                .queryParam("sort", "distance")
                .queryParam("title", "행운의 편지 3")
                .queryParam("direction", "asc")
                .queryParam("authorName", "ad")
                .queryParam("size", size)
                .queryParam("page", page)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/home")
                .then()
                .log().all()
                .statusCode(200)
                // 1. content 배열의 크기가 예상한대로 여야 한다.
                .body("boardPeeks.content.size()", equalTo(size))

                // 2. 모든 category가 "spot" 또는 "general"이여야 한다.
                .body("boardPeeks.content.category",
                        everyItem(anyOf(equalTo("SPOT"), equalTo("GENERAL"))))

                // 3. "PROMOTION" 카테고리가 포함되면 안된다.
                .body("boardPeeks.content.category", not(hasItem("PROMOTION")))

                // 4. 제목이 모두 "행운의 편지 3"을 포함해야 한다.
                .body("boardPeeks.content.title", everyItem(containsString("행운의 편지 3")));


    }

}