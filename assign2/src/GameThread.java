
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import proj.*;


public class GameThread extends Thread {

    //private static final int MAX_NUM_TRIES = 6; // Maximum number of tries for each player
    private List<Socket> sockets;
    String gameId;
    List<Player> gamePlayers;
    private BufferedReader reader;
    private PrintWriter writer;

    public GameThread(List <Player> userSockets, String gameId, List<Player> gamePlayers){
        //this.sockets = userSockets;
        /*try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        this.gameId = gameId;
        this.gamePlayers = gamePlayers;
    }

    public void run() {
        System.out.println("gamee");
    }

    /*private String chooseWord() {
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
    }*/


    /*public void updateNumOfTries(){
        lock.lock();
        int numOfTries = 1;
        lock.unlock();
    }*/
}