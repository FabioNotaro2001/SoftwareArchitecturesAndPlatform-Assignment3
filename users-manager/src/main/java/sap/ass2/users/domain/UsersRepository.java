package sap.ass2.users.domain;

import java.util.List;
import java.util.Optional;
import sap.ddd.Repository;

/** Describes the operations that the users service can do with its repository. */
public interface UsersRepository extends Repository {
    public void saveUser(User user) throws RepositoryException;
    public List<User> getUsers() throws RepositoryException;
    public Optional<User> getUserByID(String id) throws RepositoryException;
}
