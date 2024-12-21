package sap.ass2.users.infrastructure;

import io.vertx.core.Vertx;
import sap.ass2.users.application.CustomKafkaListener;
import sap.ass2.users.application.UsersManagerAPI;

/** Class responsible for UsersManagerVerticle deployment.*/
public class UsersManagerController {
    private int port;
    private UsersManagerVerticle service;

    public UsersManagerController(int port){
        this.port = port;
    }
    
    public void init(UsersManagerAPI usersAPI, CustomKafkaListener listener){
        this.service = new UsersManagerVerticle(this.port, usersAPI, listener);
        Vertx v = Vertx.vertx();
        v.deployVerticle(this.service);
    }
}