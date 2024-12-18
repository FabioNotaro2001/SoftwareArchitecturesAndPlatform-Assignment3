package sap.ass2.ebikes.domain;

import sap.ddd.Entity;

public class Ebike implements Entity<String> {
    private String id;

    public enum EbikeState {
        AVAILABLE,
        IN_USE,
        MAINTENANCE,
        DISMISSED
    }

    private EbikeState state;
    private P2d loc;
    private V2d direction;
    private double speed;
    private int batteryLevel;

    public Ebike(String id) {
        this.id = id;
        this.state = EbikeState.AVAILABLE;
        this.loc = P2d.zero();
        direction = new V2d(1, 0);
        speed = 0;
        batteryLevel = 100;
    }

    public Ebike(String id, P2d pos) {
        this.id = id;
        this.state = EbikeState.AVAILABLE;
        this.loc = pos;
        direction = new V2d(1, 0);
        speed = 0;
        batteryLevel = 100;
    }

    public Ebike(String id, EbikeState eState, P2d loc, V2d direction, double speed, int batteryLevel) {
        this.id = id;
        this.state = eState;
        this.loc = loc;
        this.direction = direction;
        this.speed = speed;
        this.batteryLevel = batteryLevel;
    }

    public String getId() {
        return id;
    }

    public EbikeState getState() {
        return state;
    }

    public void rechargeBattery() {
        batteryLevel = 100;
        state = EbikeState.AVAILABLE;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel < 0 ? 0 : (batteryLevel > 100 ? 100 : batteryLevel);

        if (batteryLevel == 0 && state != EbikeState.DISMISSED) {
            state = EbikeState.MAINTENANCE;
        } else if (batteryLevel > 0 && state == EbikeState.MAINTENANCE) {
            state = EbikeState.AVAILABLE;
        }
    }

    public void decreaseBatteryLevel(int delta) {
        batteryLevel -= delta;
        if (batteryLevel < 0) {
            batteryLevel = 0;
            state = EbikeState.MAINTENANCE;
        }
    }

    public boolean isAvailable() {
        return state == EbikeState.AVAILABLE;
    }

    public boolean isInUse() {
        return state == EbikeState.IN_USE;
    }

    public void updateState(EbikeState state) {
        this.state = state;
    }

    public void updateLocation(P2d newLoc) {
        loc = newLoc;
    }

    public void updateSpeed(double speed) {
        this.speed = speed;
    }

    public void updateDirection(V2d dir) {
        this.direction = dir;
    }

    public double getSpeed() {
        return speed;
    }

    public V2d getDirection() {
        return direction;
    }

    public P2d getLocation() {
        return loc;
    }

    public String toString() {
        return "{ id: " + id + ", loc: " + loc + ", batteryLevel: " + batteryLevel + ", state: " + state + " }";
    }

    public void applyEvent(EbikeEvent event) {
        if (this.id.equals(event.ebikeId())) {
            if (event.newState().isPresent()) {
                this.state = event.newState().get();
            }

            this.loc = this.loc.sum(event.deltaPos());
            this.direction = this.direction.sum(event.deltaDir());
            this.speed += event.deltaSpeed();
            this.batteryLevel += event.deltaBatteryLevel();
        }
    }
}
