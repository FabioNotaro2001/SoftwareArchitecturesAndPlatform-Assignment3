package sap.ass2.ebikes.application;

import java.net.URL;
import io.vertx.core.Future;

/** Interface that describes the operations that the current service (ebikes service) can do with the registry service.*/
public interface RegistryRemoteAPI {
    Future<Void> registerEbikesManager(String name, URL address);
}