package sap.ass2.ebikes.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.kafka.core.KafkaTemplate;

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
    private static final String EBIKE_EVENTS_TOPIC = "ebike-events";

    // private final EbikesRepository ebikeRepository;
    private final List<Ebike> ebikes;
    private List<EbikeEventObserver> observers; // observer = EbikesManagerVerticle.
    private KafkaTemplate<String, String> kafkaTemplate;

    public EbikesManagerImpl(EbikesRepository ebikeRepository, KafkaTemplate<String, String> kafkaTemplate) throws RepositoryException {
        this.ebikes = Collections.synchronizedList(ebikeRepository.getEbikes());
        this.observers = Collections.synchronizedList(new ArrayList<>());
        this.kafkaTemplate = kafkaTemplate;
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

    private static JsonObject ebikeEventToJSON(String ebikeId, Optional<Ebike.EbikeState> newState, V2d deltaPos, V2d deltaDir, double deltaSpeed, double deltaBatteryLevel) {
        return new JsonObject()
            .put("ebikeId", ebikeId)
            .put("newState", newState.orElse(null))
            .put("deltaPosX", deltaPos.x())
            .put("deltaPosY", deltaPos.y())
            .put("deltaDirX", deltaDir.x())
            .put("deltaDirY", deltaDir.y())
            .put("deltaSpeed", deltaSpeed)
            .put("deltaBatteryLevel", deltaBatteryLevel);
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
        // this.ebikeRepository.saveEbikeEvent(ebike);
        this.kafkaTemplate.send(EBIKE_EVENTS_TOPIC, ebikeEventToJSON(ebike.getId(), Optional.of(ebike.getState()), 
            ebike.getLocation().toV2d(), ebike.getDirection(), ebike.getSpeed(), ebike.getBatteryLevel()).encode());
        this.ebikes.add(ebike);
        this.notifyObserversAboutUpdate(ebike); // FIXME: tutti gli observer devono ascoltare per gli eventi (e fare la somma di tutte quelle cose)
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
        // this.ebikeRepository.saveEbikeEvent(ebike);
        this.kafkaTemplate.send(EBIKE_EVENTS_TOPIC, ebikeEventToJSON(ebike.getId(), Optional.of(ebike.getState()), V2d.zero(), V2d.zero(), 0, 0).encode());

        this.notifyObserversAboutRemoval(ebike.getId()); // FIXME: tutti gli observer devono ascoltare per gli eventi (e fare la somma di tutte quelle cose)

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

        var newState = Optional.<Ebike.EbikeState>empty();
        if (state.isPresent()) {
            newState = Optional.of(state.get());
            ebike.updateState(state.get());
        }

        var deltaPos = V2d.zero();
        if (locationX.isPresent()) {
            deltaPos = deltaPos.sum(new V2d(locationX.get() - ebike.getLocation().x(), 0));
            ebike.updateLocation(new P2d(locationX.get(), ebike.getLocation().y()));
        }
        if (locationY.isPresent()) {
            deltaPos = deltaPos.sum(new V2d(0, locationY.get() - ebike.getLocation().y()));
            ebike.updateLocation(new P2d(ebike.getLocation().x(), locationY.get()));
        }

        var deltaDir = V2d.zero();
        if (directionX.isPresent()) {
            deltaDir = deltaDir.sum(new V2d(directionX.get() - ebike.getDirection().x(), 0));
            ebike.updateDirection(new V2d(directionX.get(), ebike.getDirection().y()));
        }
        if (directionY.isPresent()) {
            deltaDir = deltaDir.sum(new V2d(0, directionY.get() - ebike.getDirection().y()));
            ebike.updateDirection(new V2d(ebike.getDirection().x(), directionY.get()));
        }

        double deltaSpeed = 0;
        if (speed.isPresent()) {
            deltaSpeed += speed.get() - ebike.getSpeed();
            ebike.updateSpeed(speed.get());
        }

        double deltaBatteryLevel = 0;
        if (batteryLevel.isPresent()) {
            deltaBatteryLevel += batteryLevel.get() - ebike.getBatteryLevel();
            ebike.setBatteryLevel(batteryLevel.get());

            if (ebike.getState() != newState.get()) {
                newState = Optional.of(ebike.getState());
            }
        }

        // this.ebikeRepository.saveEbikeEvent(ebike);
        this.kafkaTemplate.send(EBIKE_EVENTS_TOPIC, ebikeEventToJSON(ebike.getId(), newState, deltaPos, deltaDir, deltaSpeed, deltaBatteryLevel).encode());

        this.notifyObserversAboutUpdate(ebike);
    }

    @Override
    public void subscribeToEbikeEvents(EbikeEventObserver observer) {
        this.observers.add(observer);
    }

}