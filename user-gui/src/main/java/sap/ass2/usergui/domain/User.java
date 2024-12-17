package sap.ass2.usergui.domain;

public record User(String id, int credit) {
    public User updateCredit(int newCredit) {
        return new User(this.id, newCredit);
    }
}
