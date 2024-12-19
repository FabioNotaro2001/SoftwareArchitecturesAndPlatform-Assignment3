package sap.ass2.usergui.library;

import java.net.URL;
import java.util.Optional;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketConnectOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import sap.ass2.usergui.domain.EbikeState;

/**
 * HTTP client that interacts to the ebikes service.
 */
public class EbikesProxy implements EbikesAPI {
    private HttpClient client;
	private Vertx vertx;	// Useful for HTTP client implementation.
	private URL appAddress;	// API gateway address.
	private WebSocket webSocket;	// We use web socket because we want to estabilish a more sophisticated connection, not a simple request/response.
	
	public EbikesProxy(URL appAddress) {
		if (Vertx.currentContext() != null) {
			vertx = Vertx.currentContext().owner();
		} else {
			vertx = Vertx.vertx();
		}
		
		this.appAddress = appAddress;
		HttpClientOptions options = new HttpClientOptions()
            .setDefaultHost(appAddress.getHost())
            .setDefaultPort(appAddress.getPort());
		client = vertx.createHttpClient(options);
	}

    @Override
    public Future<JsonArray> getAllAvailableEbikesIDs() {
        Promise<JsonArray> p = Promise.promise();
		client
		.request(HttpMethod.GET, "/api/ebikes/ids")
		.onSuccess(req -> {
			req.response().onSuccess(response -> {
				response.body().onSuccess(buf -> {
					JsonObject obj = buf.toJsonObject();
					p.complete(obj.getJsonArray("ebikes"));	// Completes the promise for the admin GUI.
				});
			});
			req.send();
		})
		.onFailure(f -> {
			p.fail(f.getMessage());
		});
		return p.future();
    }

	@Override
	public Future<JsonObject> subscribeToEbikeEvents(String ebikeID, EbikeEventObserver observer) {	// observer = user gui.
		Promise<JsonObject> p = Promise.promise();
		
		// Web socket configuration. We use web socket because we want to estabilish a more sophisticated connection, not a simple request/response.
		WebSocketConnectOptions wsoptions = new WebSocketConnectOptions()
				  .setHost(this.appAddress.getHost())
				  .setPort(this.appAddress.getPort())
				  .setURI("/api/ebikes/" + ebikeID + "/events")
				  .setAllowOriginHeader(false);
		
		client
		.webSocket(wsoptions)
		.onComplete(res -> {	// Waiting for web socket opening.
            if (res.succeeded()) {
                this.webSocket = res.result();
                System.out.println("Connected!");
                this.webSocket.textMessageHandler(data -> {
                    JsonObject obj = new JsonObject(data);
                    String evType = obj.getString("event");

					// Event type discovery.
                    if (evType.equals("subscription-started")) {
                        // The message contains the ebike to watch.
						JsonObject ebike = obj.getJsonObject("ebike");
                        p.complete(ebike);
                    } else if (evType.equals("ebike-update")) {
                        // Bike parameters to change.
						Optional<String> newState = Optional.ofNullable(obj.getString("newState"));
                        double deltaLocX = obj.getDouble("deltaPosX");
                        double deltaLocY = obj.getDouble("deltaPosY");
                        double deltaDirX = obj.getDouble("deltaDirX");
                        double deltaDirY = obj.getDouble("deltaDirY");
                        double delstaSpeed = obj.getDouble("deltaSpeed");
                        int deltaBatteryLevel = obj.getInteger("deltaBatteryLevel");
                        
						// Notify event to the user GUI.
                        observer.bikeUpdated(ebikeID, newState.map(EbikeState::valueOf), deltaLocX, deltaLocY, deltaDirX, deltaDirY, delstaSpeed, deltaBatteryLevel);
                    }
                });
            } else {
                p.fail(res.cause());
            }
		});
		
		return p.future();
	}

	@Override
	public void unsubscribeFromEbikeEvents() {
		// Writes a message to the ebikes proxy of the ebike service asking for unsubscription.
        this.webSocket.writeTextMessage("unsubscribe")
			.onComplete(h -> {
				this.webSocket.close();
				this.webSocket = null;
			});
	}
}