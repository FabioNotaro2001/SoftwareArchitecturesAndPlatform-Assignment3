package sap.ass2.ebikes.domain;

import java.util.Optional;

public record EbikeEvent(String ebikeId, Optional<Ebike.EbikeState> newState, V2d deltaPos, V2d deltaDir, double deltaSpeed, double deltaBatteryLevel) {
    public static EbikeEvent from(String ebikeId, Optional<Ebike.EbikeState> newState, V2d deltaPos, V2d deltaDir, double deltaSpeed, double deltaBatteryLevel) {
        return new EbikeEvent(ebikeId, newState, deltaPos, deltaDir, deltaSpeed, deltaBatteryLevel);
    }
}