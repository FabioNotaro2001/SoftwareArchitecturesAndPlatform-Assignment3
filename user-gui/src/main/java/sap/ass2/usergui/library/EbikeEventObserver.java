package sap.ass2.usergui.library;

import sap.ass2.usergui.domain.EbikeState;

/** Interface for who listens to the ebikes events (user GUI). */
public interface EbikeEventObserver {
    void bikeUpdated(String ebikeID, EbikeState state, double locationX, double locationY, double directionX, double directionY, double speed, int batteryLevel);
}
