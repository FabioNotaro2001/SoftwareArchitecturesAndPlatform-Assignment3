package sap.ass2.ebikes.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import sap.ass2.ebikes.domain.Ebike.EbikeState;
import sap.ass2.ebikes.domain.Ebike;
import sap.ass2.ebikes.domain.EbikeEventObserver;
import sap.ass2.ebikes.domain.EbikesRepository;
import sap.ass2.ebikes.domain.P2d;
import sap.ass2.ebikes.domain.RepositoryException;
import sap.ass2.ebikes.domain.V2d;

public class EbikesManagerImpl implements EbikesManagerAPI {
    private final EbikesRepository ebikeRepository;
    private final List<Ebike> ebikes;
    private List<EbikeEventObserver> observers; // observer = EbikesManagerVerticle.

    public EbikesManagerImpl(EbikesRepository repository) throws RepositoryException {
        this.ebikeRepository = repository;
        this.ebikes = Collections.synchronizedList(ebikeRepository.getEbikes());
        this.observers = Collections.synchronizedList(new ArrayList<>());
    }

    // Converts an ebike to a JSON.
    private static JsonObject toJSON(Ebike ebike) {
        return new JsonObject()
            .put("ebikeId", ebike.getId())
            .put("state", ebike.getState().toString())
            .put("x", ebike.getLocation().x())
            .put("y", ebike.getLocation().y())
            .put("dirX", ebike.getDirection().x())
            .put("dirY", ebike.getDirection().y())
            .put("speed", ebike.getSpeed())
            .put("batteryLevel", ebike.getBatteryLevel());
    }

    @Override
    public JsonArray getAllEbikes() {
        return ebikes.stream().map(EbikesManagerImpl::toJSON).collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
    }

    private void notifyObserversAboutUpdate(Ebike ebike) {
        this.observers.forEach(o -> o.ebikeUpdated(ebike.getId(), ebike.getState(), 
            ebike.getLocation().x(), ebike.getLocation().y(), 
            ebike.getDirection().x(), ebike.getDirection().y(), ebike.getSpeed(), 
            ebike.getBatteryLevel()));
    }
    
    private void notifyObserversAboutRemoval(String ebikeID) {
        this.observers.forEach(o -> o.ebikeRemoved(ebikeID));
    }

    @Override
    public JsonArray getAllAvailableEbikesIDs() {
        return ebikes.stream().filter(Ebike::isAvailable).map(Ebike::getId).collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
    }

    @Override
    public JsonObject createEbike(String ebikeID, double locationX, double locationY) throws RepositoryException, IllegalArgumentException {
        if (this.ebikes.stream().anyMatch(ebike -> ebike.getId().equals(ebikeID))) {
            throw new IllegalArgumentException("Ebike with given id already exists.");
        }

        var ebike = new Ebike(ebikeID, new P2d(locationX, locationY));
        this.ebikeRepository.saveEbike(ebike);
        this.ebikes.add(ebike);
        this.notifyObserversAboutUpdate(ebike);
        return toJSON(ebike);
    }

    @Override
    public void removeEbike(String ebikeID) throws RepositoryException, IllegalArgumentException {
        var ebikeOpt = this.ebikes.stream().filter(ebike -> ebike.getId().equals(ebikeID)).findFirst();
        if (ebikeOpt.isEmpty()) { 
            throw new IllegalArgumentException("No ebike with id " + ebikeID);
        }

        var ebike = ebikeOpt.get();
        if (ebike.isInUse()) {
            throw new IllegalStateException("Unable to remove ebike " + ebikeID + ": currently in use");
        }

        ebike.updateState(EbikeState.DISMISSED);
        this.ebikeRepository.saveEbike(ebike);

        this.notifyObserversAboutRemoval(ebike.getId());

        this.ebikes.remove(ebike);
    }

    @Override
    public Optional<JsonObject> getEbikeByID(String ebikeID) {
        return this.ebikes.stream().filter(ebike -> ebike.getId().equals(ebikeID)).findFirst().map(EbikesManagerImpl::toJSON);
    }

    @Override
    public void updateEbike(String ebikeID, Optional<EbikeState> state, Optional<Double> locationX,
                            Optional<Double> locationY, Optional<Double> directionX, Optional<Double> directionY,
                            Optional<Double> speed, Optional<Integer> batteryLevel) throws RepositoryException, IllegalArgumentException {
        var ebikeOpt = this.ebikes.stream().filter(ebike -> ebike.getId().equals(ebikeID)).findFirst();
        if (ebikeOpt.isEmpty()) { 
            throw new IllegalArgumentException("No ebike with id " + ebikeID);
        }

        var ebike = ebikeOpt.get();

        if (state.isPresent()) {
            ebike.updateState(state.get());
        }
        if (locationX.isPresent()) {
            ebike.updateLocation(new P2d(locationX.get(), ebike.getLocation().y()));
        }
        if (locationY.isPresent()) {
            ebike.updateLocation(new P2d(ebike.getLocation().x(), locationY.get()));
        }
        if (directionX.isPresent()) {
            ebike.updateDirection(new V2d(directionX.get(), ebike.getDirection().y()));
        }
        if (directionY.isPresent()) {
            ebike.updateDirection(new V2d(ebike.getDirection().x(), directionY.get()));
        }
        if (speed.isPresent()) {
            ebike.updateSpeed(speed.get());
        }
        if (batteryLevel.isPresent()) {
            ebike.setBatteryLevel(batteryLevel.get());
        }

        this.ebikeRepository.saveEbike(ebike);

        this.notifyObserversAboutUpdate(ebike);
    }

    @Override
    public void subscribeToEbikeEvents(EbikeEventObserver observer) {
        this.observers.add(observer);
    }

}
