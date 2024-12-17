package sap.ass2.rides.application;

import java.net.URL;
import java.util.Optional;
import io.vertx.core.Future;

// Interface that describes the operations that can be done with the service registry.
public interface RegistryRemoteAPI {
    /**
     * Registers the ride microservice on the registry, in order to be discoverable from other services.
     * @param name
     * @param address
     * @return
     */
    Future<Void> registerRidesManager(String name, URL address);

    /**
     * Discovers the ebikes microservice URL.
     * @param name
     * @return
     */
    Future<Optional<String>> lookupEbikesManager(String name);

    /**
     * Discovers the users microservice URL.
     * @param name
     * @return
     */
    Future<Optional<String>> lookupUsersManager(String name);
}