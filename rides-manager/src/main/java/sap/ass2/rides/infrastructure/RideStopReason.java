package sap.ass2.rides.infrastructure;

public enum RideStopReason {
    RIDE_STOPPED("Ride stopped."),
    EBIKE_RAN_OUT_OF_BATTERY("Ebike ran out of battery."), 
    USER_RAN_OUT_OF_CREDIT("User ran out of credit."),
    SERVICE_ERROR("Failed to contact other services.");

    public final String reason;

    private RideStopReason(String reason) {
        this.reason = reason;
    }
}
