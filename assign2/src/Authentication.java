
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class Authentication {

    public Authentication() {

    }

    // Method to generate and return a new token for a player
    public static String generateToken(String playerName) {
        String token = generateRandomToken(); // Generate a random token
        Server.userTokens.put(playerName, token); // Store token-to-player mapping
        return token;
    }

    // Method to validate a token and return the associated player name
    public String getPlayerName(String token) {
        if (Server.userTokens.containsValue(token)) {  //acho que nao é a melhor forma
            return Server.userTokens.get(token);
        }
        return null; // Token not found, invalid or expired
    }

    // Method to remove a token when a player logs out
    public void removeToken(String playerName) {
        Server.userTokens.remove(playerName);
    }

    // Method to authenticate a player with a username and password
    public static boolean authenticatePlayer(String username, String password) {
        if(Server.lockDB.tryLock()) {
            if(Server.users.containsKey(username)){
                Player player = Server.users.get(username);
                //conferir se username e passe estao certas e (FALTA) que o player nao esta ja logado
                if(player.getUsername().equals(username) && player.getPassword().equals(password)) {
                    Server.lockDB.unlock();
                    return true;
                }
            }

        }
        Server.lockDB.unlock();
        return false;
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