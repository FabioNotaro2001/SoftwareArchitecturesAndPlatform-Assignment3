package sap.ass2.rides;

import java.net.URL;
import sap.ass2.rides.application.EbikesManagerRemoteAPI;
import sap.ass2.rides.application.RidesManagerAPI;
import sap.ass2.rides.application.RidesManagerImpl;
import sap.ass2.rides.application.UsersManagerRemoteAPI;
import sap.ass2.rides.infrastructure.RidesManagerController;

public class RidesManagerService {
    private RidesManagerAPI ridesManager;
    private RidesManagerController ridesController;
    private URL localAddress;

    public RidesManagerService(URL localAddress, UsersManagerRemoteAPI usersManager, EbikesManagerRemoteAPI ebikesManager) {
        this.localAddress = localAddress;

        this.ridesManager = new RidesManagerImpl(usersManager, ebikesManager);
    }

    public void launch(){
        // Starts the ride controller (so that it can start the RidesExecutionVerticle).
        this.ridesController = new RidesManagerController(this.localAddress.getPort());
        this.ridesController.init(this.ridesManager);
    }
}
