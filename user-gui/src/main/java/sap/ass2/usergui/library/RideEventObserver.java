package sap.ass2.usergui.library;

/** Interface for who listens to the rides events (user GUI). */
public interface RideEventObserver {
    void rideStep(String rideID, double x, double y, double directionX, double directionY, double speed, int batteryLevel);
    void rideEnded(String rideID, String reason);
}
