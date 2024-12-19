package sap.ass2.admingui.domain;

import java.util.Optional;

public record Ebike(String id, EbikeState state, double locX, double locY, double dirX, double dirY, double speed, int batteryLevel) {
    public Ebike addEvent( Optional<EbikeState> newState, double deltaLocX, double deltaLocY, double deltaDirX, double deltaDirY, double deltaSpeed, int deltaBatteryLevel) {
        return new Ebike(id, newState.orElse(state), locX+deltaLocX, locY+deltaLocY, dirX+deltaDirX, dirY+deltaDirY, speed+deltaSpeed, batteryLevel+deltaBatteryLevel);
    }
}
