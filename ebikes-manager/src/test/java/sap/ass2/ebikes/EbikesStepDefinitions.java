package sap.ass2.ebikes;

import io.cucumber.java.AfterAll;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

/**
 * Step definition class for Cucumber-based integration tests of the Ebike microservice.
 * Uses RestAssured for HTTP requests and Testcontainers for managing a containerized deployment.
 */
public class EbikesStepDefinitions {
    /**
     * Testcontainers setup to manage the Ebikes microservice container.
     * - `ebikesContainer` starts the containerized microservice on port 9200.
     * - Waits until the `/api/ebikes` endpoint returns HTTP 200 to ensure the service is ready.
     */
    @SuppressWarnings("resource")
    private static final GenericContainer<?> ebikesContainer = new GenericContainer<>("ebikes:latest")
            .withExposedPorts(9200) // Exposes port 9200 for external access.
            .waitingFor(Wait.forHttp("/api/ebikes").forStatusCode(200)); // Waits for readiness on `/api/ebikes`.
    
    private Response response; // Stores the HTTP response for assertions in test steps.

    /**
     * Step: Verifies that the microservice is running.
     * - Starts the container if it's not already running.
     * - Configures RestAssured with the base URL and port of the running container.
     */
    @Given("the microservice is running")
    public void the_microservice_is_running() {
        if (!ebikesContainer.isRunning()) { // Checks if the container is already running.
            ebikesContainer.start(); // Starts the container.
            RestAssured.baseURI = "http://" + ebikesContainer.getHost(); // Sets the base URL for RestAssured.
            RestAssured.port = ebikesContainer.getMappedPort(9200); // Maps the container port to the host port.
        }
    }

    /**
     * Step: Sends a GET request to the specified endpoint.
     * - Uses RestAssured to make the request.
     * @param endpoint The endpoint to send the GET request to.
     */
    @When("I request GET {string}")
    public void i_request_get(String endpoint) {
        response = RestAssured.get(endpoint); // Executes the GET request and stores the response.
    }

    /**
     * Step: Sends a POST request to the specified endpoint with a JSON body.
     * - Uses RestAssured to make the POST request.
     * @param endpoint The endpoint to send the POST request to.
     * @param body The JSON body of the POST request.
     */
    @When("I POST to {string} with body:")
    public void i_post_to_with_body(String endpoint, String body) {
        response = RestAssured.given() // Prepares the POST request.
                .contentType("application/json") // Sets the content type to JSON.
                .body(body) // Attaches the request body.
                .post(endpoint); // Sends the POST request to the specified endpoint.
    }

    /**
     * Step: Verifies that the HTTP response status matches the expected status.
     * @param expectedStatus The expected HTTP status code.
     */
    @Then("the response status should be {int}")
    public void the_response_status_should_be(int expectedStatus) {
        assertThat(response.getStatusCode(), equalTo(expectedStatus)); // Asserts that the response status matches.
    }

    /**
     * Step: Verifies that the response body contains a specific string.
     * @param content The expected content string to be present in the response body.
     */
    @Then("the response body should contain {string}")
    public void the_response_body_should_contain(String content) {
        assertThat(response.getBody().asString(), containsString(content)); // Asserts the presence of the content.
    }

    /**
     * After all tests are executed:
     * - Stops the container if it is still running.
     * - Ensures cleanup of resources.
     */
    @AfterAll
    public static void tearDown() {
        if (ebikesContainer.isRunning()) { // Checks if the container is running.
            ebikesContainer.stop(); // Stops the container to release resources.
        }
    }
}
