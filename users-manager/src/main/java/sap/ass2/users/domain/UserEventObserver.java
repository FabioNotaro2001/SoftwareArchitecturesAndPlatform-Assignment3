package sap.ass2.users.domain;

/** Interface for who listens to the users events (UsersManager and UsersManagerVerticle). */
public interface UserEventObserver {
    void userUpdated(String userID, int credit);
}
