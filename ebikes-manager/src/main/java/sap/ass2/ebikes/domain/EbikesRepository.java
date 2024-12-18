package sap.ass2.ebikes.domain;

import java.util.List;
import java.util.Optional;

import sap.ddd.Repository;

/** Describes the operations that the ebikes service can do with its repository. */
public interface EbikesRepository extends Repository {
    void saveEbikeEvent(EbikeEvent event) throws RepositoryException;

    List<Ebike> getEbikes() throws RepositoryException; 

    Optional<Ebike> getEbikeByID(String id) throws RepositoryException; 
}
