package sap.ass2.registry.infrastructure;

import java.util.logging.Logger;
import io.vertx.core.Vertx;
import sap.ass2.registry.domain.RegistryAPI;

// Deploy the registey verticle.
public class RegistryController {
    private int port;
    private RegistryVerticle service;
    static Logger logger = Logger.getLogger("[Registry Controller]");	

    public RegistryController(int port){
        this.port = port;
    }
    
    public void init(RegistryAPI registryAPI){
        this.service = new RegistryVerticle(this.port, registryAPI);
        Vertx v = Vertx.vertx();
        v.deployVerticle(service);
    }
}
