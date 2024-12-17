package sap.ass2.users.domain;

public class UserBuilder {
    private String userId = "";
    private int credit = 0;
    
    public UserBuilder() {

    }

    public void applyEvent(UserEvent event) {
        if (userId.equals("")) {
            this.userId = event.userId();
        } else if (!userId.equals(event.userId())) {
            return;
        }
        
        this.credit += event.creditDelta();
    }

    public User build() {
        return new User(userId, credit);
    }
}