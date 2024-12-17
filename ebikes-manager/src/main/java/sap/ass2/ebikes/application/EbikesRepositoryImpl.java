package sap.ass2.ebikes.application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import io.vertx.core.json.JsonObject;
import sap.ass2.ebikes.domain.Ebike;
import sap.ass2.ebikes.domain.Ebike.EbikeState;
import sap.ass2.ebikes.domain.EbikesRepository;
import sap.ass2.ebikes.domain.P2d;
import sap.ass2.ebikes.domain.RepositoryException;
import sap.ass2.ebikes.domain.V2d;

public class EbikesRepositoryImpl implements EbikesRepository {
    private String dbaseFolder;            

    public EbikesRepositoryImpl() {
        this.dbaseFolder =  "./database";  
        makeDir(dbaseFolder);  
    }

	// Save the given JSON object in the db folder.
    private void saveObj(String id, JsonObject obj) throws RepositoryException {
		try {
			FileWriter fw = new FileWriter(Path.of(dbaseFolder, id + ".json").toString());
			java.io.BufferedWriter wr = new BufferedWriter(fw);	
		
			wr.write(obj.encodePrettily());
			wr.flush();
			fw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RepositoryException(); 
		}
	}

	private void makeDir(String name) {
		try {
			File dir = new File(name);
			if (!dir.exists()) {
				dir.mkdir();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/** // Converts the given bike into a JSON object and then save it into the DB. */
    @Override
    public void saveEbike(Ebike eBike) throws RepositoryException {
		JsonObject obj = new JsonObject();
		obj.put("ID", eBike.getId());
		obj.put("STATE", eBike.getState().toString());
		obj.put("LOC_X", eBike.getLocation().x());
		obj.put("LOC_Y", eBike.getLocation().y());
		obj.put("DIR_X", eBike.getDirection().x());
		obj.put("DIR_Y", eBike.getDirection().y());
		obj.put("SPEED", eBike.getSpeed());
		obj.put("BATTERY", eBike.getBatteryLevel());

		this.saveObj(eBike.getId(), obj);
    }

    @Override
    public List<Ebike> getEbikes() throws RepositoryException {
        List<Ebike> ebikes = new ArrayList<>();
		File ebikeDir = new File(dbaseFolder);  
		
		if (ebikeDir.exists() && ebikeDir.isDirectory()) {
			File[] ebikeFiles = ebikeDir.listFiles((dir, name) -> name.endsWith(".json"));  
			
			if (ebikeFiles != null) {
				for (File ebikeFile : ebikeFiles) {
					try {
						String content = new String(Files.readAllBytes(ebikeFile.toPath()));
						JsonObject obj = new JsonObject(content);
						
						Ebike ebike = new Ebike(
							obj.getString("ID"),
							EbikeState.valueOf(obj.getString("STATE")),
							new P2d(obj.getDouble("LOC_X"), obj.getDouble("LOC_Y")),
							new V2d(obj.getDouble("DIR_X"), obj.getDouble("DIR_Y")),
							obj.getDouble("SPEED"),
							obj.getInteger("BATTERY")
						);
						ebikes.add(ebike);
					} catch (IOException e) {
						throw new RepositoryException(); 
					}
				}
			}
		}
		return ebikes;  
    }

    @Override
    public Optional<Ebike> getEbikeByID(String id) throws RepositoryException {
        File ebikeFile = new File(Path.of(dbaseFolder, id + ".json").toString());

		if (!ebikeFile.exists()) {
			return Optional.empty();
		} else {
			try {
				String content = new String(Files.readAllBytes(ebikeFile.toPath()));
				JsonObject obj = new JsonObject(content);

				return Optional.of(new Ebike(
					obj.getString("ID"),
					EbikeState.valueOf(obj.getString("STATE")),
					new P2d(obj.getDouble("LOC_X"), obj.getDouble("LOC_Y")),
					new V2d(obj.getDouble("DIR_X"), obj.getDouble("DIR_Y")),
					obj.getDouble("SPEED"),
					obj.getInteger("BATTERY")
				));
			} catch (IOException e) {
				throw new RepositoryException();  
			}
		}
    }

}
