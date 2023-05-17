
import java.io.*;
import java.net.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    protected static int port;
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
        port = Integer.parseInt(args[0]);

        //ler o ficheiro e guardar no map
        users = Registration.readUserFile();
        executor = Executors.newFixedThreadPool(poolsize);

        try  {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            ServerSocket serverSocket = serverSocketChannel.socket();
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
                    List<Player> gamePlayers = new ArrayList<>();
                    for (int i = 0; i < 3; i++) {
                        gamePlayers.add(playersQueue.poll());
                    }

                    String gameId = UUID.randomUUID().toString();
                    //Server.playersInGame.put(gameId, gamePlayers);
                    /*for (Player player : gamePlayers) {
                        Server.userCurrentGame.put(player.getUsername(), gameId);
                    }*/
                    executor.submit(new GameThread(gamePlayers, gameId));
                }
                SocketChannel clientChannel = serverSocketChannel.accept();
                Socket clientSocket = clientChannel.socket();
                AuthenticationThread thread = new AuthenticationThread(clientSocket);
                System.out.println("Starting new Thread");
                thread.start();


            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}