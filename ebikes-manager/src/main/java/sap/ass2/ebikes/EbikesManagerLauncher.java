package sap.ass2.ebikes;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import sap.ass2.ebikes.application.RegistryProxy;
import sap.ass2.ebikes.application.RegistryRemoteAPI;
import sap.ass2.ebikes.domain.RepositoryException;

public class EbikesManagerLauncher {
    private static final String EBIKES_MANAGER_NAME = "ebikes-manager";

    // Get external configurations (externalized configuration pattern).
    private static final String SERVICE_ADDRESS = System.getenv("EBIKES_URL");
    private static final String REGISTRY_ADDRESS = System.getenv("REGISTRY_URL");

    public static void main(String[] args) throws MalformedURLException, URISyntaxException, RepositoryException{
        URL localAddress = URI.create(SERVICE_ADDRESS).toURL();
        
        // Start the internal bikes service.
        EbikesManagerService service = new EbikesManagerService(localAddress);
        service.launch();   

        // Register the internal bikes service on the registry service.
        RegistryRemoteAPI registry = new RegistryProxy(URI.create(REGISTRY_ADDRESS).toURL());
        registry.registerEbikesManager(EBIKES_MANAGER_NAME, localAddress);  
    }
}