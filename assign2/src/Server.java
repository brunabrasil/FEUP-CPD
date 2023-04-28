
import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This program demonstrates a simple TCP/IP socket server.
 *
 * @author www.codejava.net
 */
public class Server {
    protected static Map<String, Player> users;
    protected static ReentrantLock lockDB = new ReentrantLock();
    protected static ReentrantLock lockPlayersQueue = new ReentrantLock();

    protected static Queue<Player> playersQueue;
    public static void main(String[] args) {

        if (args.length < 1) return;
        int port = Integer.parseInt(args[0]);

        //ler o ficheiro e guardar no map
        users = Registration.readUserFile();

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);

            while (true) {
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