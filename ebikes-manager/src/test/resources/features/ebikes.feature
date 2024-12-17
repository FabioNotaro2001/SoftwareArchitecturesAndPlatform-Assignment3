Feature: Ebikes Manager Component Tests

Scenario: Get all ebikes
    Given the microservice is running
    When I request GET "/api/ebikes"
    Then the response status should be 200
    And the response body should contain "ebikes"

Scenario: Create a new ebike
    Given the microservice is running
    When I POST to "/api/ebikes" with body:
        """
        { "ebikeId": "1234", "x": 10.0, "y": 20.0 }
        """
    Then the response status should be 200
    And the response body should contain "1234"
