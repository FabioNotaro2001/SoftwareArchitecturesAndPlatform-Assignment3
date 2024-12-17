package sap.ass2.usergui.library;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface RidesAPI {
    Future<JsonObject> beginRide(String userID, String bikeID);
    Future<Void> stopRide(String rideID, String userID);
    
    /**
     * Allows the user GUI to subscribe to the events of the specified ride.
     * @param rideId
     * @param observer
     * @return
     */
    Future<JsonObject> subscribeToRideEvents(String rideId, RideEventObserver observer);
    
    /**
     * Allows the user GUI to unsubscribe from the current ride events (for example because it is ended).
     */
    void unsubscribeFromRideEvents();
}