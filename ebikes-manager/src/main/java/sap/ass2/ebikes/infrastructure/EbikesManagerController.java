package sap.ass2.ebikes.infrastructure;

import io.vertx.core.Vertx;
import sap.ass2.ebikes.application.EbikesManagerAPI;

/** Class responsible for EbikesManagerVerticle deployment.*/
public class EbikesManagerController {
    private int port;
    private EbikesManagerVerticle service;

    public EbikesManagerController(int port){
        this.port = port;
    }
    
    public void init(EbikesManagerAPI ebikesAPI){
        this.service = new EbikesManagerVerticle(this.port, ebikesAPI);
        Vertx v = Vertx.vertx();
        v.deployVerticle(this.service);
    }
}
