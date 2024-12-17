package sap.ass2.registry;

import java.net.URL;
import sap.ass2.registry.domain.RegistryAPI;
import sap.ass2.registry.domain.RegistryImpl;
import sap.ass2.registry.infrastructure.RegistryController;

public class RegistryService {
    private RegistryAPI registry;
    private RegistryController registryController;
    private URL localAddress;

    public RegistryService(URL localAddress) {
        this.localAddress = localAddress;
        this.registry = new RegistryImpl("registry");
    }

    public void launch(){
        this.registryController = new RegistryController(this.localAddress.getPort());
        this.registryController.init(this.registry);
    }
}
