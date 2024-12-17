package sap.ass2.registry;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class RegistryLauncher {
    private static final String SERVICE_ADDRESS = System.getenv("REGISTRY_URL");    // Get the service address from the environment variable of the deploymnent infrastructure (external configuration pattern).

    public static void main(String[] args) throws MalformedURLException, URISyntaxException {
        URL localAddress = URI.create(SERVICE_ADDRESS).toURL();
        RegistryService service = new RegistryService(localAddress);
        service.launch();
    }
}
