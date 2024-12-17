package sap.ass2.apigateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// If the circuit breaker finds out that a service is not reachable, it use the following fallbacks.
@RestController
public class FallbackController {
    @GetMapping("/fallback/registry")
    public String registryFallback() {
        return "Il servizio Registry non è al momento disponibile. Riprova più tardi.";
    }

    @GetMapping("/fallback/users")
    public String usersFallback() {
        return "Il servizio Users Manager non è al momento disponibile. Riprova più tardi.";
    }

    @GetMapping("/fallback/ebikes")
    public String ebikesFallback() {
        return "Il servizio Ebikes Manager non è al momento disponibile. Riprova più tardi.";
    }

    @GetMapping("/fallback/rides")
    public String ridesFallback() {
        return "Il servizio Rides Manager non è al momento disponibile. Riprova più tardi.";
    }

}
