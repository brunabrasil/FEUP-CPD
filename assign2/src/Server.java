
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This program demonstrates a simple TCP/IP socket server.
 *
 * @author www.codejava.net
 */
public class Server {
    private static int poolsize = 5;
    private static ExecutorService executor;
    protected static Map<String, Player> users;
    protected static ReentrantLock lockDB = new ReentrantLock();
    protected static ReentrantLock lockPlayersQueue = new ReentrantLock();
    protected static ReentrantLock lockToken = new ReentrantLock();
    protected static HashMap<String, String> userTokens  = new HashMap<>(); // Map to store token-to-player mapping
    protected static Queue<Player> playersQueue = new LinkedList<>();
    private static Map<String, String> userCurrentGame = new HashMap<>();
    private static Map<String, List<Player>> playersInGame = new HashMap<>();

    public static void main(String[] args) {

        if (args.length < 1) return;
        int port = Integer.parseInt(args[0]);

        //ler o ficheiro e guardar no map
        users = Registration.readUserFile();
        executor = Executors.newFixedThreadPool(poolsize);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                AuthenticationThread thread = new AuthenticationThread(socket);
                System.out.println("Starting new Thread");
                thread.start();

                /*
                // check if there are enough players to start a game
                if (Server.playersQueue.size() >= 3) {
                    // create a new game thread
                    List<Player> gamePlayers = new ArrayList<>();
                    for (int i = 0; i < 3; i++) {
                        gamePlayers.add(Server.playersQueue.remove(0));
                    }
                    String gameId = UUID.randomUUID().toString();
                    Server.playersInGame.put(gameId, gamePlayers);
                    for (Player player : gamePlayers) {
                        Server.userCurrentGame.put(player.getUsername(), gameId);
                    }
                    executor.submit(new Game(gamePlayers));
                }*/
            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}