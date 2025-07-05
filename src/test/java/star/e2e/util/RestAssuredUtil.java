package star.e2e.util;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import star.e2e.dto.request.E2ERequest;

public final class RestAssuredUtil {

    public static ValidatableResponse sendRequest(E2ERequest requestDTO) {
        return given()
                .log().all()
                .header("Authorization", "Bearer " + requestDTO.accessToken())
                .contentType(ContentType.JSON)
                .body(requestDTO.body() == null ? "" : requestDTO.body())
                .when()
                .request(requestDTO.method(), requestDTO.url())
                .then()
                .log().all()
                .statusCode(requestDTO.expectedStatusCode());
    }

}
