

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class Registration {


    public static final String REGISTRATION_FILE = "registration.txt"; // File to store registration data

    // Method to register a new user with a username and password
    public static String registerUser(String username, String password) {
        //username ja existe
        if(Server.lockDB.tryLock()) {
            if (Server.users.containsKey(username)) {
                Server.lockDB.unlock();
                return "failed - Username already exists";
            } else {

                try (FileWriter writer = new FileWriter(REGISTRATION_FILE, true)) {
                    // Append the username and password to the registration file
                    writer.write(username + "," + password + System.lineSeparator());
                    writer.flush(); //limpa a stream
                    // adicionar ao map
                    Server.users.put(username, new Player(username, password));
                    Server.lockDB.unlock();
                    return "success";
                } catch (IOException e) {
                    System.out.println("Failed to register user");
                    Server.lockDB.unlock();
                    return "failed ";
                }


            }
        }
        Server.lockDB.unlock();
        return "failed";
    }

    public static Map<String, Player> readUserFile() {
        Map<String, Player> users = new HashMap<>();

        File file = new File(REGISTRATION_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating file");
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(REGISTRATION_FILE))) {
            String line;
            while (( line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 2) {
                    users.put(data[0], new Player(data[0], data[1]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading data");
        }
        return users;
    }
}