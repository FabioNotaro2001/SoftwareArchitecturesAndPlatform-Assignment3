package sap.ass2.usergui.library;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public interface EbikesAPI {
    Future<JsonArray> getAllAvailableEbikesIDs();

    /**
     * Allows the user GUI to subscribe to the events of a specified bike.
     * @param ebikeID
     * @param observer
     * @return
     */
    Future<JsonObject> subscribeToEbikeEvents(String ebikeID, EbikeEventObserver observer);
    
    /**
     * Allows the user GUI to unsubscribe from the current bike events (for example because the ride is ended).
     */
    void unsubscribeFromEbikeEvents();
}