
import java.io.*;
import java.net.*;
import java.util.Map;


/**
 * This program demonstrates a simple TCP/IP socket server.
 *
 * @author www.codejava.net
 */
public class Server {
    private static Map<String, Player> users;

    public static void main(String[] args) {

        if (args.length < 1) return;
        int port = Integer.parseInt(args[0]);

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