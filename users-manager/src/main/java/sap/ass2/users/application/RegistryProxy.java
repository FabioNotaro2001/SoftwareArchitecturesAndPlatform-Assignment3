package sap.ass2.users.application;

import java.net.URL;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

/**
 * HTTP client that interacts to the registry service for the operations useful to the users service.
 */
public class RegistryProxy implements RegistryRemoteAPI{
    private HttpClient client;	
	private Vertx vertx;	// Useful for HTTP client implementation.
	
	public RegistryProxy(URL registryAddress) {
		vertx = Vertx.vertx();
		HttpClientOptions options = new HttpClientOptions()
            .setDefaultHost(registryAddress.getHost())
            .setDefaultPort(registryAddress.getPort());
		client = vertx.createHttpClient(options);
	}

	// The users service doesn't need to know anything about the other services, so it simply registers itself on the registry.
	@Override
	public Future<Void> registerUsersManager(String name, URL address) {
		Promise<Void> p = Promise.promise();
		client
		.request(HttpMethod.POST, "/api/registry/users-manager")
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
}