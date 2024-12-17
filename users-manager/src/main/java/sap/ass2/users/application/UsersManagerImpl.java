package sap.ass2.users.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import sap.ass2.users.domain.RepositoryException;
import sap.ass2.users.domain.User;
import sap.ass2.users.domain.UserEventObserver;
import sap.ass2.users.domain.UsersRepository;

public class UsersManagerImpl implements UsersManagerAPI {
    private final UsersRepository userRepository;
    private final List<User> users;
    private List<UserEventObserver> observers;  // observer = UsersManagerVerticle.

    public UsersManagerImpl(UsersRepository userRepository) throws RepositoryException {
        this.userRepository = userRepository;
        this.observers = Collections.synchronizedList(new ArrayList<>());
        this.users = Collections.synchronizedList(userRepository.getUsers());
    }

    // Converts an user to a JSON.
    private static JsonObject toJSON(User user) {
        return new JsonObject()
            .put("userId", user.getId())
            .put("credit", user.getCredit());
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
        this.userRepository.saveUser(user);
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
        this.userRepository.saveUser(user); 
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
        this.userRepository.saveUser(user); 
        this.notifyObservers(user); 
    }

    @Override
    public void subscribeToUserEvents(UserEventObserver observer) {
        this.observers.add(observer);
    }
}
