package sap.ass2.admingui.library;

import java.net.URL;
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
import sap.ass2.admingui.domain.EbikeState;

/**
 * HTTP client that interacts to the ebikes service.
 */
public class EbikesProxy implements EbikesAPI {
    private HttpClient client;
	private Vertx vertx;	// Useful for HTTP client implementation.
	private URL appAddress;	// API gateway address.
	
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
    public Future<JsonArray> getAllEbikes() {
        Promise<JsonArray> p = Promise.promise();
		client
		.request(HttpMethod.GET, "/api/ebikes")
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
    public Future<JsonObject> createEbike(String ebikeID, double locationX, double locationY) {
        Promise<JsonObject> p = Promise.promise();
		client
		.request(HttpMethod.POST, "/api/ebikes")
		.onSuccess(req -> {
			req.response().onSuccess(response -> {
				response.body().onSuccess(buf -> {
					JsonObject obj = buf.toJsonObject();
					p.complete(obj.getJsonObject("ebike"));	// Completes the promise for the admin GUI.
				});
			});

			// Request setup before sending.
            req.putHeader("content-type", "application/json");
			JsonObject body = new JsonObject();
			body.put("ebikeId", ebikeID);
			body.put("x", locationX);
			body.put("y", locationY);
			String payload = body.encodePrettily();
		    req.putHeader("content-length", "" + payload.length());
			req.write(payload);
			req.send();
		})
		.onFailure(f -> {
			p.fail(f.getMessage());
		});
		return p.future();
    }

    @Override
    public Future<Void> removeEbike(String ebikeID) {
        Promise<Void> p = Promise.promise();
		client
		.request(HttpMethod.DELETE, "/api/ebikes/" + ebikeID)
		.onSuccess(req -> {
			req.response().onSuccess(response -> {
				response.body().onSuccess(buf -> {
					p.complete();	// Completes the promise for the admin GUI.
				});
			});

			// Request setup before sending.
            req.putHeader("content-type", "application/json");
			JsonObject body = new JsonObject();
			body.put("ebikeId", ebikeID);
			String payload = body.encodePrettily();
		    req.putHeader("content-length", "" + payload.length());
			req.write(payload);
			req.send();
		})
		.onFailure(f -> {
			p.fail(f.getMessage());
		});
		return p.future();
    }

    @Override
    public Future<JsonArray> subscribeToEbikeEvents(EbikeEventObserver observer) {	// observer = admin gui.
        Promise<JsonArray> p = Promise.promise();
		
		// Web socket configuration. We use web socket because we want to estabilish a more sophisticated connection, not a simple request/response.
		WebSocketConnectOptions wsoptions = new WebSocketConnectOptions()
				  .setHost(this.appAddress.getHost())
				  .setPort(this.appAddress.getPort())
				  .setURI("/api/ebikes/events")
				  .setAllowOriginHeader(false);
		
		client
		.webSocket(wsoptions)
		.onComplete(res -> {	// Waiting for web socket opening.
            if (res.succeeded()) {
                WebSocket ws = res.result();
                System.out.println("Connected!");
                ws.textMessageHandler(data -> {
                    JsonObject obj = new JsonObject(data);
                    String evType = obj.getString("event");

					// Event type discovery.
                    if (evType.equals("subscription-started")) {
						// The message contains the ebikes array.
                        JsonArray ebikes = obj.getJsonArray("ebikes");
                        p.complete(ebikes);
                    } else if (evType.equals("ebike-update")) {
						// Bike parameters to change.
                        String ebikeID = obj.getString("ebikeId");
                        String state = obj.getString("state");
                        double locX = obj.getDouble("x");
                        double locY = obj.getDouble("y");
                        double dirX = obj.getDouble("dirX");
                        double dirY = obj.getDouble("dirY");
                        double speed = obj.getDouble("speed");
                        int batteryLevel = obj.getInteger("batteryLevel");
                        
						// Notify event to the admin GUI.
                        observer.bikeUpdated(ebikeID, EbikeState.valueOf(state), locX, locY, dirX, dirY, speed, batteryLevel);
                    } else if (evType.equals("ebike-remove")) {
                        String ebikeID = obj.getString("ebikeId");
                        
						// Notify event to the admin GUI.
                        observer.bikeRemoved(ebikeID);
                    }
                });
            } else {
                p.fail(res.cause());
            }
		});
		
		return p.future();
    }

}
