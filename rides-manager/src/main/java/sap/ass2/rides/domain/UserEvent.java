package sap.ass2.rides.domain;

public record UserEvent(String userId, int creditDelta) {
    public static UserEvent from(String userId, int creditDelta) {
        return new UserEvent(userId, creditDelta);
    }
}