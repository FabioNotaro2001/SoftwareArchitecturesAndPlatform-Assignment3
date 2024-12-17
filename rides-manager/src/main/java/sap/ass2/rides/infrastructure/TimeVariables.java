package sap.ass2.rides.infrastructure;

public record TimeVariables(long lastTimeDecreasedCredit, long lastTimeChangedDir, long lastTimeBatteryDecreased) {
    public static TimeVariables now() {
        var now = System.currentTimeMillis();
        return new TimeVariables(now, now, now);
    }

    public TimeVariables updateLastTimeDecreasedCredit(long newTime) {
        return new TimeVariables(newTime, this.lastTimeChangedDir, this.lastTimeBatteryDecreased);
    }

    public TimeVariables updateLastTimeChangedDir(long newTime) {
        return new TimeVariables(this.lastTimeDecreasedCredit, newTime, this.lastTimeBatteryDecreased);
    }

    public TimeVariables updateLastTimeBatteryDecreased(long newTime) {
        return new TimeVariables(this.lastTimeDecreasedCredit, this.lastTimeChangedDir, newTime);
    }
}
