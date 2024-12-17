package sap.ass2.admingui.domain;

public record User(String id, int credit) {
    public User updateCredit(int newCredit) {
        return new User(this.id, newCredit);
    }
}
