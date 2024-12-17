package sap.ass2.admingui.library;

import sap.ass2.admingui.domain.EbikeState;

/** Interface for who listens to the ebikes events (admin GUI). */
public interface EbikeEventObserver {
    void bikeUpdated(String bikeID, EbikeState state, double locationX, double locationY, double directionX, double directionY, double speed, int batteryLevel);
    void bikeRemoved(String bikeID);
}
