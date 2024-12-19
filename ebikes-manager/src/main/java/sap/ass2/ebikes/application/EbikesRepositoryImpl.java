package sap.ass2.ebikes.application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.vertx.core.json.JsonObject;

import java.util.function.Function;

import sap.ass2.ebikes.domain.Ebike;
import sap.ass2.ebikes.domain.Ebike.EbikeState;
import sap.ass2.ebikes.domain.EbikeBuilder;
import sap.ass2.ebikes.domain.EbikeEvent;
import sap.ass2.ebikes.domain.EbikesRepository;
import sap.ass2.ebikes.domain.RepositoryException;
import sap.ass2.ebikes.domain.V2d;

public class EbikesRepositoryImpl implements EbikesRepository, EbikeEventsConsumer {
	private Path dbFile;
	Optional<Map<String, Ebike>> ebikes;

	public EbikesRepositoryImpl() throws RepositoryException {
		this.dbFile = Path.of("./database/db.txt");
		try {
			new File(this.dbFile.toString()).createNewFile();
		} catch (IOException e) {
			throw new RepositoryException();
		}

		this.ebikes = Optional.empty();
	}

	// Save the given JSON object in the db folder.
	// private void saveObj(String id, JsonObject obj) throws RepositoryException {
	// try {
	// FileWriter fw = new FileWriter(Path.of(dbaseFolder, id +
	// ".json").toString());
	// java.io.BufferedWriter wr = new BufferedWriter(fw);

	// wr.write(obj.encodePrettily());
	// wr.flush();
	// fw.close();
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// throw new RepositoryException();
	// }
	// }

	// private void makeDir(String name) {
	// try {
	// File dir = new File(name);
	// if (!dir.exists()) {
	// dir.mkdir();
	// }
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// }

	/**
	 * // Converts the given bike into a JSON object and then save it into the DB.
	 */
	@Override
	public void saveEbikeEvent(EbikeEvent event) throws RepositoryException {
		SpecialFileParser parser = new SpecialFileParser(this.dbFile);
		try {
			parser.addEvent(event);

			if (this.ebikes.isEmpty()) {
				return;
			}
			var ebike = Optional.ofNullable(ebikes.get().get(event.ebikeId()));
			if (ebike.isEmpty()) {
				return;
			}
			ebike.get().applyEvent(event);
		} catch (IOException e) {
			throw new RepositoryException();
		}
	}

	private void loadEbikes() throws RepositoryException {
		SpecialFileParser parser = new SpecialFileParser(this.dbFile);
		try {
			var ebikeEvents = new ArrayList<>(parser.getEbikeEvents());
			var builders = ebikeEvents.stream()
					.map(ev -> ev.ebikeId())
					.distinct()
					.collect(Collectors.toMap(
							Function.identity(),
							id -> new EbikeBuilder()));
			ebikeEvents.stream()
					.forEach(ev -> builders.get(ev.ebikeId()).applyEvent(ev));
			this.ebikes = Optional.of(builders.values().stream()
					.map(EbikeBuilder::build)
					.collect(Collectors.toMap(
							Ebike::getId,
							Function.identity())));
		} catch (IOException e) {
			throw new RepositoryException();
		}
	}

	@Override
	public List<Ebike> getEbikes() throws RepositoryException {
		// List<Ebike> ebikes = new ArrayList<>();
		// File ebikeDir = new File(dbaseFolder);

		// if (ebikeDir.exists() && ebikeDir.isDirectory()) {
		// File[] ebikeFiles = ebikeDir.listFiles((dir, name) ->
		// name.endsWith(".json"));

		// if (ebikeFiles != null) {
		// for (File ebikeFile : ebikeFiles) {
		// try {
		// String content = new String(Files.readAllBytes(ebikeFile.toPath()));
		// JsonObject obj = new JsonObject(content);

		// Ebike ebike = new Ebike(
		// obj.getString("ID"),
		// EbikeState.valueOf(obj.getString("STATE")),
		// new P2d(obj.getDouble("LOC_X"), obj.getDouble("LOC_Y")),
		// new V2d(obj.getDouble("DIR_X"), obj.getDouble("DIR_Y")),
		// obj.getDouble("SPEED"),
		// obj.getInteger("BATTERY")
		// );
		// ebikes.add(ebike);
		// } catch (IOException e) {
		// throw new RepositoryException();
		// }
		// }
		// }
		// }
		// return ebikes;

		if (ebikes.isEmpty()) {
			loadEbikes();
		}

		return this.ebikes.get().values().stream().filter(eb -> eb.getState() != EbikeState.DISMISSED).toList();
	}

	@Override
	public Optional<Ebike> getEbikeByID(String id) throws RepositoryException {
		// File ebikeFile = new File(Path.of(dbaseFolder, id + ".json").toString());

		// if (!ebikeFile.exists()) {
		// return Optional.empty();
		// } else {
		// try {
		// String content = new String(Files.readAllBytes(ebikeFile.toPath()));
		// JsonObject obj = new JsonObject(content);

		// return Optional.of(new Ebike(
		// obj.getString("ID"),
		// EbikeState.valueOf(obj.getString("STATE")),
		// new P2d(obj.getDouble("LOC_X"), obj.getDouble("LOC_Y")),
		// new V2d(obj.getDouble("DIR_X"), obj.getDouble("DIR_Y")),
		// obj.getDouble("SPEED"),
		// obj.getInteger("BATTERY")));
		// } catch (IOException e) {
		// throw new RepositoryException();
		// }
		// }

		if (ebikes.isEmpty()) {
			loadEbikes();
		}

		var ebike = Optional.ofNullable(this.ebikes.get().get(id));

		return ebike.isPresent() && ebike.get().getState() != EbikeState.DISMISSED ? ebike : Optional.empty();
	}

	@Override
	public void consumeEvents(String message) {
		JsonObject obj = new JsonObject(message);
		try {
			saveEbikeEvent(EbikeEvent.from(obj.getString("ebikeId"), Optional.ofNullable(obj.getString("newState")).map(EbikeState::valueOf),
				new V2d(obj.getDouble("deltaPosX"),obj.getDouble("deltaPosY")), new V2d(obj.getDouble("deltaDirX"),obj.getDouble("deltaDirY")),
				obj.getDouble("deltaSpeed"), obj.getDouble("deltaBatteryLevel")));
		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
