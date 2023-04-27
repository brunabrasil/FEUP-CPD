
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class Authentication {
    private static HashMap<String, String> tokenToPlayerMap; // Map to store token-to-player mapping

    public Authentication() {
        tokenToPlayerMap = new HashMap<>();
    }

    // Method to generate and return a new token for a player
    public static String generateToken(String playerName) {
        String token = generateRandomToken(); // Generate a random token
        tokenToPlayerMap.put(token, playerName); // Store token-to-player mapping
        return token;
    }

    // Method to validate a token and return the associated player name
    public String getPlayerName(String token) {
        if (tokenToPlayerMap.containsKey(token)) {
            return tokenToPlayerMap.get(token);
        }
        return null; // Token not found, invalid or expired
    }

    // Method to remove a token when a player logs out or disconnects
    public void removeToken(String token) {
        tokenToPlayerMap.remove(token);
    }

    // Method to authenticate a player with a username and password
    public static boolean authenticatePlayer(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(Registration.REGISTRATION_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true; // Username and password match
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to authenticate player: " + e.getMessage());
        }
        return false; // Username and password do not match or not found
    }

    // Method to generate a random token (example implementation)
    private static String generateRandomToken() {

        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int TOKEN_LENGTH = 10;

        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < TOKEN_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            char c = CHARACTERS.charAt(index);
            sb.append(c);
        }

        return sb.toString();
    }
}