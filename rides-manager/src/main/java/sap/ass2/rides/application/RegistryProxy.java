package sap.ass2.rides.application;

import java.net.URL;
import java.util.Optional;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

/**
 * HTTP client that interacts to the registry service for the operations useful to the rides service.
 */
public class RegistryProxy implements RegistryRemoteAPI{
    private HttpClient client;	
	private Vertx vertx;	// Useful for HTTP client implementation.
	
	public RegistryProxy(URL registryAddress) {
		if (Vertx.currentContext() != null) {
			vertx = Vertx.currentContext().owner();
		} else {
			vertx = Vertx.vertx();
		}
		HttpClientOptions options = new HttpClientOptions()
            .setDefaultHost(registryAddress.getHost())
            .setDefaultPort(registryAddress.getPort());
		client = vertx.createHttpClient(options);
	}

	@Override
	public Future<Void> registerRidesManager(String name, URL address) {
		Promise<Void> p = Promise.promise();
		client
		.request(HttpMethod.POST, "/api/registry/rides-manager")
		.onSuccess(req -> {
			req.response().onSuccess(response -> {
				response.body().onSuccess(buf -> {
					p.complete();
				});
			});

			// Request setup before sending to the registry.
			req.putHeader("content-type", "application/json");
			JsonObject body = new JsonObject();
			body.put("name", name);
			body.put("address", address.toString());
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
    public Future<Optional<String>> lookupUsersManager(String name) {
        Promise<Optional<String>> p = Promise.promise();
		client
		.request(HttpMethod.GET, "/api/registry/users-manager/" + name)
		.onSuccess(req -> {
			req.response().onSuccess(response -> {
				response.body().onSuccess(buf -> {
					JsonObject obj = buf.toJsonObject();
					p.complete(Optional.ofNullable(obj.getString("usersManager")));	// Completes the promise for the rides manager.
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
    public Future<Optional<String>> lookupEbikesManager(String name) {
        Promise<Optional<String>> p = Promise.promise();
		client
		.request(HttpMethod.GET, "/api/registry/ebikes-manager/" + name)
		.onSuccess(req -> {
			req.response().onSuccess(response -> {
				response.body().onSuccess(buf -> {
					JsonObject obj = buf.toJsonObject();
					p.complete(Optional.ofNullable(obj.getString("ebikesManager")));	// Completes the promise for the rides manager.
				});
			});
			req.send();
		})
		.onFailure(f -> {
			p.fail(f.getMessage());
		});
		return p.future();
    }
}