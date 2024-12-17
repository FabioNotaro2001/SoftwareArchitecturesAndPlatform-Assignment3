package sap.ass2.ebikes;

import java.net.URL;
import sap.ass2.ebikes.application.EbikesManagerAPI;
import sap.ass2.ebikes.application.EbikesManagerImpl;
import sap.ass2.ebikes.application.EbikesRepositoryImpl;
import sap.ass2.ebikes.domain.RepositoryException;
import sap.ass2.ebikes.infrastructure.EbikesManagerController;

public class EbikesManagerService {
    private EbikesManagerAPI ebikesManager;
    private EbikesManagerController ebikesController;
    private URL localAddress;

    public EbikesManagerService(URL localAddress) throws RepositoryException{
        this.localAddress = localAddress;
        this.ebikesManager = new EbikesManagerImpl(new EbikesRepositoryImpl());
    }

    public void launch(){
        // Starts the ebike controller, so that it can starts the service verticle.
        this.ebikesController = new EbikesManagerController(this.localAddress.getPort());
        this.ebikesController.init(this.ebikesManager);
    }
}
