package sap.ass2.admingui.library;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

public interface UsersAPI {
    Future<JsonArray> getAllUsers();

    /**
     * Allows the admin GUI to subscribe to the users eventa.
     * @param observer
     * @return
     */
    Future<JsonArray> subscribeToUsersEvents(UserEventObserver observer);
}
