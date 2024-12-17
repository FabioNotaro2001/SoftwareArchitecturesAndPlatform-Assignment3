package sap.ass2.rides;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import io.vertx.core.Future;
import sap.ass2.rides.application.EbikesManagerProxy;
import sap.ass2.rides.application.EbikesManagerRemoteAPI;
import sap.ass2.rides.application.RegistryProxy;
import sap.ass2.rides.application.RegistryRemoteAPI;
import sap.ass2.rides.application.UsersManagerProxy;
import sap.ass2.rides.application.UsersManagerRemoteAPI;

public class RidesManagerLauncher {
    private static final String RIDES_MANAGER_NAME = "rides-manager";

    // Get external configurations (externalized configuration patter).
    private static final String SERVICE_ADDRESS = System.getenv("RIDES_URL");
    private static final String REGISTRY_ADDRESS = System.getenv("REGISTRY_URL");

    public static void main(String[] args) throws MalformedURLException, URISyntaxException {
        URL localAddress = URI.create(SERVICE_ADDRESS).toURL();

        RegistryRemoteAPI registry = new RegistryProxy(URI.create(REGISTRY_ADDRESS).toURL());
        
        // Queries the registry service to discover the user and ebikes services.
        var usersFut = registry.lookupUsersManager("users-manager");
        var ebikesFut = registry.lookupEbikesManager("ebikes-manager");

        Future.all(usersFut, ebikesFut)
            .onSuccess(cf -> {
                List<Optional<String>> results = cf.list();

                var usersManagerAddressOpt = results.get(0);
                if (usersManagerAddressOpt.isEmpty()) {
                    System.err.println("Users manager not found.");
                    System.exit(1);
                }
                UsersManagerRemoteAPI usersManager = null;
                try {
                    // Create the proxy (outbound port) to interact with users service.
                    usersManager = new UsersManagerProxy(URI.create(usersManagerAddressOpt.get()).toURL());
                } catch (MalformedURLException e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
                
                var ebikesManagerAddressOpt = results.get(1);
                if (ebikesManagerAddressOpt.isEmpty()) {
                    System.err.println("Ebikes manager not found.");
                    System.exit(1);
                }
                EbikesManagerRemoteAPI ebikesManager = null;
                try {
                    // Create the proxy (outbound port) to interact with ebikes service.
                    ebikesManager = new EbikesManagerProxy(URI.create(ebikesManagerAddressOpt.get()).toURL());
                } catch (MalformedURLException e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
                
                // Start the internal rides service.
                RidesManagerService service = new RidesManagerService(localAddress, usersManager, ebikesManager);
                service.launch();
                
                // Register the internal rides service on the registry service.
                registry.registerRidesManager(RIDES_MANAGER_NAME, localAddress);
            });
    }
}
