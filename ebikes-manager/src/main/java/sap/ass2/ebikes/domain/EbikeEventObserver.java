package sap.ass2.ebikes.domain;

import sap.ass2.ebikes.domain.Ebike.EbikeState;

/** Interface for who listens to the bikes events (EbikesManager and EbikesManagerVerticle). */
public interface EbikeEventObserver {
    void ebikeUpdated(String ebikeID, EbikeState state, double locationX, double locationY, double directionX, double directionY, double speed, int batteryLevel);
    void ebikeRemoved(String ebikeID);
}
