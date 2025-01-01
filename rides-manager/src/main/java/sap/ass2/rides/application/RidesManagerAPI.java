package sap.ass2.rides.application;

import java.util.Optional;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Java interface implemented by RidesManagerImpl.
 */
public interface RidesManagerAPI {
    Future<JsonArray> getAllRides();
    Future<JsonObject> beginRide(String userID, String ebikeID) throws IllegalArgumentException;
    Future<Void> stopRide(String rideID, String userID) throws IllegalArgumentException;
    Future<Optional<JsonObject>> getRideByRideID(String rideID);
    Future<Optional<JsonObject>> getRideByEbikeID(String ebikeID);
    Future<Optional<JsonObject>> getRideByUserID(String userID);
}