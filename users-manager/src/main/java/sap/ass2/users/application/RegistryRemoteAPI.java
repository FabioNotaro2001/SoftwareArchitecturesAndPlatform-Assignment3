package sap.ass2.users.application;

import java.net.URL;
import io.vertx.core.Future;

/** Interface that describes the operations that the current service (users service) can do with the registry service.*/
public interface RegistryRemoteAPI {
    Future<Void> registerUsersManager(String name, URL address);
}