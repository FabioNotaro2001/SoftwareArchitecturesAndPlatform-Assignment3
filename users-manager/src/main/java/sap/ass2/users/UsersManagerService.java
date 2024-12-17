package sap.ass2.users;

import java.net.URL;
import sap.ass2.users.application.UsersRepositoryImpl;
import sap.ass2.users.application.KafkaProducerConfig;
import sap.ass2.users.application.UsersManagerAPI;
import sap.ass2.users.application.UsersManagerImpl;
import sap.ass2.users.domain.RepositoryException;
import sap.ass2.users.infrastructure.UsersManagerController;

public class UsersManagerService {
    private UsersManagerAPI usersManager;
    private UsersManagerController usersController;
    private URL localAddress;

    public UsersManagerService(URL localAddress) throws RepositoryException{
        this.localAddress = localAddress;
        this.usersManager = new UsersManagerImpl(new UsersRepositoryImpl(), KafkaProducerConfig.kafkaTemplate());
    }

    public void launch(){
        // Starts the users controller, so that it can starts the service verticle.
        this.usersController = new UsersManagerController(this.localAddress.getPort());
        this.usersController.init(this.usersManager);
    }
}
