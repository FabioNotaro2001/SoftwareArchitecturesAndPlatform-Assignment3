package sap.ass2.usergui.library;

/** Interface for who listens to the users events (user GUI). */
public interface UserEventObserver {
    void userUpdated(String userID, int credit);
}
