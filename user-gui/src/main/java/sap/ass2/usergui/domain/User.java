package sap.ass2.usergui.domain;

public record User(String id, int credit) {
    public User updateCredit(int creditChange) {
        return new User(this.id, this.credit+creditChange);
    }
}
