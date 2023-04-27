
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class AuthenticationThread extends Thread {

    private Socket socket;
    private ReentrantLock lock = new ReentrantLock();
    private String playerId; // Player ID or username

    public AuthenticationThread(Socket userSockets){
        this.socket = userSockets;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String message = reader.readLine();

            if(Objects.equals(message.split(" ")[0], "login")){

                String username = message.split(" ")[1];
                String password = message.split(" ")[2];

                if(Authentication.authenticatePlayer(username, password)){

                    String token = Authentication.generateToken(username);
                    OutputStream output = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);
                    writer.println("Login successfuly " + token);
                }
                else {
                    OutputStream output = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);
                    writer.println("login failed");
                }

            } else if (Objects.equals(message.split(" ")[0], "register")){
                String username = message.split(" ")[1];
                String password = message.split(" ")[2];
                Registration.registerUser(username, password);

            } else {
                System.out.println("Invalid option");
            }

            lock.lock();
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            //writer.println(Server.numOfTries);
            lock.unlock();

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}