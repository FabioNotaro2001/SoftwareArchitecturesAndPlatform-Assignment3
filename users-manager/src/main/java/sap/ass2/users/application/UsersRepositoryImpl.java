package sap.ass2.users.application;

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
import sap.ass2.users.domain.RepositoryException;
import sap.ass2.users.domain.User;
import sap.ass2.users.domain.UsersRepository;

public class UsersRepositoryImpl implements UsersRepository {
    private String dbaseFolder;            

    public UsersRepositoryImpl() {
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

	/**
	 * // Converts the given user into a JSON object and then save it into the DB.
	 */
    @Override
    public void saveUser(User user) throws RepositoryException {
		JsonObject obj = new JsonObject();
		obj.put("ID", user.getId());
		obj.put("CREDIT", user.getCredit());

		this.saveObj(user.getId(), obj);
    }

    @Override
    public List<User> getUsers() throws RepositoryException {
        List<User> users = new ArrayList<>();
		File userDir = new File(dbaseFolder);  
		
		if (userDir.exists() && userDir.isDirectory()) {
			File[] userFiles = userDir.listFiles((dir, name) -> name.endsWith(".json"));  
			
			if (userFiles != null) {
				for (File userFile : userFiles) {
					try {
						String content = new String(Files.readAllBytes(userFile.toPath()));
						JsonObject obj = new JsonObject(content);
						
						User user = new User(obj.getString("ID"), obj.getInteger("CREDIT"));
						users.add(user);
					} catch (IOException e) {
						throw new RepositoryException(); 
					}
				}
			}
		}
		return users; 
    }

    @Override
    public Optional<User> getUserByID(String id) throws RepositoryException {
        File userFile = new File(Path.of(dbaseFolder, id + ".json").toString());

		if (!userFile.exists()) {
			return Optional.empty();  
		} else {
			try {
				String content = new String(Files.readAllBytes(userFile.toPath()));
				JsonObject obj = new JsonObject(content);
				
				return Optional.of(new User(obj.getString("ID"), obj.getInteger("CREDIT")));
			} catch (IOException e) {
				throw new RepositoryException();  
			}
		}
    }
}
