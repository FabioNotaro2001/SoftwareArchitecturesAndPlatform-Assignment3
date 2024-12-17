package sap.ass2.admingui.library;

/**
 * Interface for accessing the various services from the GUIs.
 */
public interface ApplicationAPI {
    UsersAPI users();
    EbikesAPI ebikes();
    RidesAPI rides();
}
