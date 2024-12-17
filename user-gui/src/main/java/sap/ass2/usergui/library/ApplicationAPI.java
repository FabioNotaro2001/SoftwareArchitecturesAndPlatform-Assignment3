package sap.ass2.usergui.library;

/**
 * Interface for accessing the various services from the GUIs.
 */
public interface ApplicationAPI {
    UsersAPI users();
    EbikesAPI ebikes();
    RidesAPI rides();
}
