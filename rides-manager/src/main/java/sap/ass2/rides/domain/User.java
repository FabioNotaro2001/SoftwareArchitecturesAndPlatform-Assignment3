package sap.ass2.rides.domain;

import sap.ddd.Entity;

public record User(String id, int credit) implements Entity<String> {
    public User updateCredit(int newCredit) {
        return new User(this.id, newCredit);
    }

    @Override
    public String getId() {
        return id;
    }
}
