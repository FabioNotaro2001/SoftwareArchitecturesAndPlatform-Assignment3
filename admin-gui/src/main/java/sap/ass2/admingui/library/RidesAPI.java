package sap.ass2.admingui.library;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

public interface RidesAPI {
    Future<JsonArray> getAllRides();

    /**
     * Allows the admin GUI to subscribe to the rides event.
     * @param observer
     * @return
     */
    Future<JsonArray> subscribeToRideEvents(RideEventObserver observer);
}