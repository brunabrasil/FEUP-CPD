
import java.io.*;
import java.net.*;
import java.time.LocalTime;
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
    private static int poolsize = 2;
    private static ExecutorService executor;
    protected static Map<String, Player> users = new HashMap<>();
    protected static ReentrantLock lockDB = new ReentrantLock();
    protected static ReentrantLock lockPlayersQueue = new ReentrantLock();
    protected static ReentrantLock lockToken = new ReentrantLock();
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
            int timesPrinted = 0;
            while (true) {
                if(LocalTime.now().getSecond() % 5 == 0 && timesPrinted == 0){
                    System.out.println("Players in queue: " + Server.playersQueue.size());
                    timesPrinted++;
                }
                if(LocalTime.now().getSecond() % 10 == 1){
                    timesPrinted = 0;
                }
                // check if there are enough players to start a game
                if (Server.playersQueue.size() >= 3) {
                    // create a new game thread
                    System.out.println(playersQueue);
                    List<Player> gamePlayers = new ArrayList<>();
                    for (int i = 0; i < 3; i++) {
                        gamePlayers.add(playersQueue.poll());
                    }

                    String gameId = UUID.randomUUID().toString();
                    Server.playersInGame.put(gameId, gamePlayers);
                    for (Player player : gamePlayers) {
                        Server.userCurrentGame.put(player.getUsername(), gameId);
                    }
                    executor.submit(new GameThread(gamePlayers, gameId, gamePlayers));
                }

                Socket socket = serverSocket.accept();
                AuthenticationThread thread = new AuthenticationThread(socket);
                System.out.println("Starting new Thread");
                thread.start();


            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}