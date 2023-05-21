
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
    protected static ExecutorService executor;
    protected static Map<String, Player> users = new HashMap<>();
    protected static ReentrantLock lockDB = new ReentrantLock();
    protected static ReentrantLock lockPlayersQueue = new ReentrantLock();
    protected static ReentrantLock lockToken = new ReentrantLock();
    protected static Queue<Player> playersQueue = new PriorityQueue<>(Comparator.comparing(Player::getRank));
    //protected static Queue<Player> playersQueue = new LinkedList<>();
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

            // Start a separate thread to handle the number of players in the queue
            Thread queueThread = new Thread(new QueueHandler());
            queueThread.start();

            while (true) {
                System.out.println("Players:");
                for (Player player : users.values()) {
                    System.out.println("player: " + player.getUsername() + " | " + player.isLoggedIn());
                }

                SocketChannel clientChannel = serverSocketChannel.accept();
                Socket clientSocket = clientChannel.socket();


                AuthenticationThread thread = new AuthenticationThread(clientSocket, clientChannel, queueThread);
                System.out.println("Starting new Thread");
                thread.start();

            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}