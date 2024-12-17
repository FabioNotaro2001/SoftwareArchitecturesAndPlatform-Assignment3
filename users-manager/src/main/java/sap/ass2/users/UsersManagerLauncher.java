package sap.ass2.users;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import sap.ass2.users.application.RegistryProxy;
import sap.ass2.users.application.RegistryRemoteAPI;
import sap.ass2.users.domain.RepositoryException;

public class UsersManagerLauncher {
    private static final String USERS_MANAGER_NAME = "users-manager";

    // Get external configurations (externalized configuration pattern).
    private static final String SERVICE_ADDRESS = System.getenv("USERS_URL");
    private static final String REGISTRY_ADDRESS = System.getenv("REGISTRY_URL");

    public static void main(String[] args) throws MalformedURLException, URISyntaxException, RepositoryException {
        URL localAddress = URI.create(SERVICE_ADDRESS).toURL();
        
        // Start the internal users service.
        UsersManagerService service = new UsersManagerService(localAddress);
        service.launch();

        // Register the internal users service on the registry service.
        RegistryRemoteAPI registry = new RegistryProxy(URI.create(REGISTRY_ADDRESS).toURL());
        registry.registerUsersManager(USERS_MANAGER_NAME, localAddress);
    }
}
