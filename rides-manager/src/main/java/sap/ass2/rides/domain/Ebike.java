package sap.ass2.rides.domain;

import sap.ddd.Entity;

public record Ebike(String id, EbikeState state, double locX, double locY, double dirX, double dirY, double speed, int batteryLevel) implements Entity<String> {
    @Override
    public String getId() {
        return id;
    }
}