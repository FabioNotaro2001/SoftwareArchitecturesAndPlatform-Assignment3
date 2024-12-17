package sap.ass2.admingui.library;

/** Interface for who listens to the rides events (admin GUI). */
public interface RideEventObserver {
    void rideStarted(String rideID, String userID, String bikeID);
    void rideStep(String rideID, double x, double y, double directionX, double directionY, double speed, int batteryLevel);
    void rideEnded(String rideID, String reason);
}
