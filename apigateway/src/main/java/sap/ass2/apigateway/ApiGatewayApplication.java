package sap.ass2.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        // Start the Spring Boot application
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        // Fetch external service URLs from environment variables for dynamic configuration (external configuration pattern).
        String registryUrl = System.getenv("REGISTRY_URL"); // URL for the registry service
        String usersUrl = System.getenv("USERS_URL");       // URL for the users service
        String ebikesUrl = System.getenv("EBIKES_URL");     // URL for the e-bikes service
        String ridesUrl = System.getenv("RIDES_URL");       // URL for the rides service
        
        // Define the API Gateway routes
        return builder.routes()
            // Route for the registry service
            .route("REGISTRY_ROUTE", r -> r.path("/api/registry/**") // Match requests starting with /api/registry
                .filters(f -> f.circuitBreaker(c -> c.setName("registryCircuitBreaker") // Configure circuit breaker
                                                    .setFallbackUri("forward:/fallback/registry"))) // Fallback URI if service fails
                .uri(registryUrl))  // Forward requests to the registry service

            // Route for the users service
            .route("USERS_MANAGER_ROUTE", r -> r.path("/api/users/**") // Match requests starting with /api/users
                .filters(f -> f.circuitBreaker(c -> c.setName("usersCircuitBreaker") // Configure circuit breaker
                                                    .setFallbackUri("forward:/fallback/users"))) // Fallback URI if service fails
                .uri(usersUrl))    // Forward requests to the users service

            // Route for the e-bikes service
            .route("EBIKES_MANAGER_ROUTE", r -> r.path("/api/ebikes/**") // Match requests starting with /api/ebikes
                .filters(f -> f.circuitBreaker(c -> c.setName("ebikesCircuitBreaker") // Configure circuit breaker
                                                    .setFallbackUri("forward:/fallback/ebikes"))) // Fallback URI if service fails
                .uri(ebikesUrl))   // Forward requests to the e-bikes service

            // Route for the rides service
            .route("RIDES_MANAGER_ROUTE", r -> r.path("/api/rides/**") // Match requests starting with /api/rides
                .filters(f -> f.circuitBreaker(c -> c.setName("ridesCircuitBreaker") // Configure circuit breaker
                                                    .setFallbackUri("forward:/fallback/rides"))) // Fallback URI if service fails
                .uri(ridesUrl))    // Forward requests to the rides service

            // Build the route configuration
            .build();
    }
}
