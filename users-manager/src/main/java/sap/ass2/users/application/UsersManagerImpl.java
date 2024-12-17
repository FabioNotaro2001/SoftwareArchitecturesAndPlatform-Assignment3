package sap.ass2.users.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.kafka.core.KafkaTemplate;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import sap.ass2.users.domain.RepositoryException;
import sap.ass2.users.domain.User;
import sap.ass2.users.domain.UserEventObserver;
import sap.ass2.users.domain.UsersRepository;

public class UsersManagerImpl implements UsersManagerAPI {
    private static final String USER_EVENTS_TOPIC = "user-events";

    private final UsersRepository userRepository;
    private final List<User> users;
    private List<UserEventObserver> observers;  // observer = UsersManagerVerticle.
    private KafkaTemplate<String, String> kafkaTemplate;

    public UsersManagerImpl(UsersRepository userRepository, KafkaTemplate<String, String> kafkaTemplate) throws RepositoryException {
        this.userRepository = userRepository;
        this.observers = Collections.synchronizedList(new ArrayList<>());
        this.users = Collections.synchronizedList(userRepository.getUsers());
        this.kafkaTemplate = kafkaTemplate;
    }

    // Converts an user to a JSON.
    private static JsonObject toJSON(User user) {
        return new JsonObject()
            .put("userId", user.getId())
            .put("credit", user.getCredit());
    }
    
    private static JsonObject userEventToJSON(String userId, int credits) {
        return new JsonObject()
            .put("userId", userId)
            .put("credits", credits);
    }

    @Override
    public JsonArray getAllUsers() {
        return users.stream().map(UsersManagerImpl::toJSON).collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
    }

    private void notifyObservers(User user) {
        this.observers.forEach(o -> o.userUpdated(user.getId(), user.getCredit()));
    }

    @Override
    public JsonObject createUser(String userID) throws RepositoryException {
        var user = new User(userID, 0);
        // this.userRepository.saveUserEvent(user);
        kafkaTemplate.send(USER_EVENTS_TOPIC, userEventToJSON(user.getId(), user.getCredit()).encode());
        this.users.add(user);
        this.notifyObservers(user);
        return UsersManagerImpl.toJSON(user);
    }

    @Override
    public Optional<JsonObject> getUserByID(String userID) {
        var user = this.users.stream().filter(u -> u.getId().equals(userID)).findFirst();
        return user.map(UsersManagerImpl::toJSON);
    }

    @Override
    public void rechargeCredit(String userID, int credit) throws RepositoryException, IllegalArgumentException {
        var userOpt = this.users.stream().filter(u -> u.getId().equals(userID)).findFirst();
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid user id");
        }

        var user = userOpt.get(); 
        user.rechargeCredit(credit); 
        // this.userRepository.saveUserEvent(user);
        kafkaTemplate.send(USER_EVENTS_TOPIC, userEventToJSON(userID, credit).encode());

        this.notifyObservers(user); 
    }

    @Override
    public void decreaseCredit(String userID, int amount) throws RepositoryException {
        var userOpt = this.users.stream().filter(u -> u.getId().equals(userID)).findFirst(); 
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid user id");
        }

        var user = userOpt.get(); 
        user.decreaseCredit(amount); 
        // this.userRepository.saveUserEvent(user);
        kafkaTemplate.send(USER_EVENTS_TOPIC, userEventToJSON(userID, -amount).encode());

        this.notifyObservers(user); 
    }

    @Override
    public void subscribeToUserEvents(UserEventObserver observer) {
        this.observers.add(observer);
    }
}
