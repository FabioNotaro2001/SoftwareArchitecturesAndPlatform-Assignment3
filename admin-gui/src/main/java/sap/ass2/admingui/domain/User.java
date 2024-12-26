package sap.ass2.admingui.domain;

public record User(String id, int credit) {
    public User updateCredit(int creditChange) {
        return new User(this.id, credit + creditChange);
    }
}
