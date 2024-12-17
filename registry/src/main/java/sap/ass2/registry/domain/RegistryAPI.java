package sap.ass2.registry.domain;

import java.net.URL;
import java.util.Optional;

// Java interface for the registry service.
public interface RegistryAPI {
    // API for registering a microservice to the registry, so it can be discovered from the other services.
    void registerUsersManager(String name, URL address);
    void registerEbikesManager(String name, URL address);
    void registerRidesManager(String name, URL address);

    // API that allows a service to discover the other services.
    Optional<String> lookupUsersManager(String name);
    Optional<String> lookupEbikesManager(String name);
    Optional<String> lookupRidesManager(String name);
}