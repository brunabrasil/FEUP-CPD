

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;


public class Registration {

    public static final String REGISTRATION_FILE = "registration.txt"; // File to store registration data

    //VERIFICAR ANTES SE JA EXISTEEEEE

    // Method to register a new user with a username and password
    public static void registerUser(String username, String password) {
        try (FileWriter writer = new FileWriter(REGISTRATION_FILE, true)) {
            // Append the username and password to the registration file
            writer.write(username + "," + password + System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            System.out.println("Failed to register user: " + e.getMessage());
        }
    }

    public static Map<String, Player> readUserFile() {
        Map<String, Player> users = null;

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