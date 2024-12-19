package sap.ass2.users.application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.Optional;
import java.util.stream.Collectors;

import io.vertx.core.json.JsonObject;
import sap.ass2.users.domain.RepositoryException;
import sap.ass2.users.domain.User;
import sap.ass2.users.domain.UserBuilder;
import sap.ass2.users.domain.UserEvent;
import sap.ass2.users.domain.UsersRepository;

public class UsersRepositoryImpl implements UsersRepository, UserEventsConsumer {
    private Path dbFile;
	Optional<Map<String, User>> users;

    public UsersRepositoryImpl() throws RepositoryException {
        this.dbFile = Path.of("./database/db.txt");  
		try {
			new File(this.dbFile.toString()).createNewFile();
		} catch (IOException e) {
			throw new RepositoryException();
		}

		this.users = Optional.empty();
    }

	// Save the given JSON object in the db folder.
    // private void saveObj(String id, JsonObject obj) throws RepositoryException { 
	// 	try {
	// 		FileWriter fw = new FileWriter(Path.of(dbaseFolder, id + ".json").toString());
	// 		java.io.BufferedWriter wr = new BufferedWriter(fw);	
		
	// 		wr.write(obj.encodePrettily());
	// 		wr.flush();
	// 		fw.close();
	// 	} catch (Exception ex) {
	// 		ex.printStackTrace();
	// 		throw new RepositoryException();  
	// 	}
	// }
	
	// private void makeDir(String name) {
	// 	try {
	// 		File dir = new File(name);
	// 		if (!dir.exists()) {
	// 			dir.mkdir();
	// 		}
	// 	} catch (Exception ex) {
	// 		ex.printStackTrace();
	// 	}
	// }

	/**
	 * // Converts the given user into a JSON object and then save it into the DB.
	 */
    @Override
    public void saveUserEvent(UserEvent event) throws RepositoryException {
		SpecialFileParser parser = new SpecialFileParser(this.dbFile);
		try {
			parser.addEvent(event);

			if (this.users.isEmpty()) {
				return;
			}
			var user = Optional.ofNullable(users.get().get(event.userId()));
			if (user.isEmpty()) {
				return;
			}
			user.get().applyEvent(event);
		} catch (IOException e) {
			throw new RepositoryException();
		}
    }

	private void loadUsers() throws RepositoryException {
		SpecialFileParser parser = new SpecialFileParser(this.dbFile);
		try {
			var userEvents = new ArrayList<>(parser.getUserEvents());
			var builders = userEvents.stream()
				.map(ev -> ev.userId())
				.distinct()
				.collect(Collectors.toMap(
					Function.identity(),
					id -> new UserBuilder()
				));
			userEvents.stream()
				.forEach(ev -> builders.get(ev.userId()).applyEvent(ev));
			this.users = Optional.of(builders.values().stream()
				.map(UserBuilder::build)
				.collect(Collectors.toMap(
					User::getId,
					Function.identity()
				)));
		} catch (IOException e) {
			throw new RepositoryException();
		}
	}

    @Override
    public List<User> getUsers() throws RepositoryException {
        // List<User> users = new ArrayList<>();
		// File userDir = new File(dbaseFolder);
		
		// if (userDir.exists() && userDir.isDirectory()) {
		// 	File[] userFiles = userDir.listFiles((dir, name) -> name.endsWith(".json"));  
			
		// 	if (userFiles != null) {
		// 		for (File userFile : userFiles) {
		// 			try {
		// 				String content = new String(Files.readAllBytes(userFile.toPath()));
		// 				JsonObject obj = new JsonObject(content);
						
		// 				User user = new User(obj.getString("ID"), obj.getInteger("CREDIT"));
		// 				users.add(user);
		// 			} catch (IOException e) {
		// 				throw new RepositoryException(); 
		// 			}
		// 		}
		// 	}
		// }

		if (users.isEmpty()) {
			loadUsers();
		}

		return this.users.get().values().stream().toList();
    }

    @Override
    public Optional<User> getUserByID(String id) throws RepositoryException {
        // File userFile = new File();

		// if (!userFile.exists()) {
		// 	return Optional.empty();  
		// } else {
		// 	try {
		// 		String content = new String(Files.readAllBytes(userFile.toPath()));
		// 		JsonObject obj = new JsonObject(content);
				
		// 		return Optional.of(new User(obj.getString("ID"), obj.getInteger("CREDIT")));
		// 	} catch (IOException e) {
		// 		throw new RepositoryException();  
		// 	}
		// }

		if (users.isEmpty()) {
			loadUsers();
		}

		return Optional.ofNullable(this.users.get().get(id));
    }

	@Override
	public void consumeEvents(String message) {
		JsonObject obj = new JsonObject(message);
		try {
			saveUserEvent(UserEvent.from(obj.getString("userId"), obj.getInteger("credits")));
		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
