import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class E2ETest {
    @BeforeAll
    public static void setup() {
        String apiGatewayUrl = System.getenv("APIGATEWAY_URL");
        RestAssured.baseURI = apiGatewayUrl;  // Uses environment variable for the API gateway.
    }

    @Test
    public void testUserJourney() {
        // Step 1: create a new user (but in this case we use an old user).
        String userId = "fabio";
        // RestAssured.given()
        //     .contentType("application/json")
        //     .body(new JsonObject().put("userId", userId).encode())
        //     .post("/api/users")
        //     .then()
        //     .statusCode(200)
        //     .extract().response();

        RestAssured.given()
            .contentType("application/json")
            .body(new JsonObject().put("userId", userId).put("credit", 1000).encode())
            .post("/api/users/"+userId+"/recharge-credit")
            .then()
            .statusCode(200)
            .extract().response();
        
        // Step 2: create a new bike.
        Response getBikeIds = RestAssured.given()
            .contentType("application/json")
            .get("/api/ebikes/ids")
            .then()
            .statusCode(200)
            .extract().response();

        String ebikeId = getBikeIds.jsonPath().getString("ebikes[0]");
        
        // // Step 3: start a new ride.
        Response getRideId = RestAssured.given()
            .contentType("application/json")
            .body(new JsonObject().put("userId", userId).put("ebikeId", ebikeId).encode())
            .post("/api/rides")
            .then()
            .statusCode(200)
            .extract().response();

        String rideId = getRideId.jsonPath().getString("ride.rideId");

        // // Step 4: stop the ride.
        RestAssured.given()
            .contentType("application/json")
            .body(new JsonObject().put("userId", userId).put("rideId", rideId).encode())
            .delete("/api/rides")
            .then()
            .statusCode(200);
    } 
}
