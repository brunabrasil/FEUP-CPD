
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import proj.*;


public class GameThread extends Thread {

    //private static final int MAX_NUM_TRIES = 6; // Maximum number of tries for each player
    // private static Map<String, GameData> gameDataMap = new ConcurrentHashMap<>(); // Map to store game data for each player

    private Socket socket;
    private ReentrantLock lock = new ReentrantLock();
    private String playerId; // Player ID or username

    public GameThread(Socket userSockets){
        this.socket = userSockets;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String wordChosen = chooseWord();
            List<Map.Entry<Character, Boolean>> charList = getCharList(wordChosen);
            int numOfTries = 3;

            String letter;
            while(!Objects.equals(letter = reader.readLine(), "exit")){
                System.out.println("Letter: "+ letter);
                //verificar se letra ta na palavra
                updateNumOfTries();
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                writer.println(Hangman.getHangman(numOfTries, charList));
            }

            lock.lock();
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(numOfTries);
            lock.unlock();

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String chooseWord() {
        // Choose a word randomly from a list
        List<String> word = Arrays.asList("Apple", "Bicycle", "Elephant", "Garden", "Monkey", "Pizza", "Rainbow", "Soccer", "Jazz", "Hamburger");
        Random random = new Random();
        int index = random.nextInt(word.size());
        return word.get(index);
    }

    private List<Map.Entry<Character, Boolean>> getCharList(String wordChosen) {
        // Generate a list of character entries for the chosen word
        List<Map.Entry<Character, Boolean>> pairs = new ArrayList<>();
        for (char c : wordChosen.toCharArray()) {
            pairs.add(new AbstractMap.SimpleEntry<>(c, false));
        }
        return pairs;
    }


    public void updateNumOfTries(){
        lock.lock();
        int numOfTries = 1;
        lock.unlock();
    }
}