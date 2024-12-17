package sap.ass2.usergui.library;

import java.net.URL;

public class ApplicationImpl implements ApplicationAPI {
    private UsersAPI users;
    private EbikesAPI ebikes;
    private RidesAPI rides;

    /**
     * 
     * @param appAddress is the API gateway address.
     */
    public ApplicationImpl(URL appAddress) {
        // Despite using the same address, they are specialized.
        this.users = new UsersProxy(appAddress);
        this.ebikes = new EbikesProxy(appAddress);
        this.rides = new RidesProxy(appAddress);
    }

    @Override
    public UsersAPI users() {
        return this.users;
    }

    @Override
    public EbikesAPI ebikes() {
        return this.ebikes;
    }

    @Override
    public RidesAPI rides() {
        return this.rides;
    }
}
