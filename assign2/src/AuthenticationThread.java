
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class AuthenticationThread extends Thread {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public AuthenticationThread(Socket userSockets){
        this.socket = userSockets;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while(true) {
            try {
                String message = reader.readLine();
                if (message.split(" ")[0].equals("login")) {

                    String username = message.split(" ")[1];
                    String password = message.split(" ")[2];

                    if (Authentication.authenticatePlayer(username, password)) {

                        String token = Authentication.generateToken(username);
                        //FALTA: verificar se a pessoa ja tem um token

                        Server.lockToken.lock();
                        Server.userTokens.put(username, token); // Store the token
                        Server.lockToken.unlock();

                        writer.println("login successfully " + token);
                        if(Server.lockPlayersQueue.tryLock()){
                            Server.playersQueue.add(new Player(username, password));
                            Server.lockPlayersQueue.unlock();

                        }
                        break;
                    } else {
                        writer.println("login failed");
                    }

                } else if (Objects.equals(message.split(" ")[0], "register")) {
                    String username = message.split(" ")[1];
                    String password = message.split(" ")[2];

                    if (Registration.registerUser(username, password).equals("success")) {
                        String token = Authentication.generateToken(username);
                        Server.lockToken.lock();
                        Server.userTokens.put(username, token); // Store the token
                        Server.lockToken.unlock();

                        writer.println("registration successfully " + token);
                        //trylock tenta uma vez e passa, é non blocking // lock fica travado e tentando ate conseguir, é blocking //decidir isso
                        if(Server.lockPlayersQueue.tryLock()){
                            Server.playersQueue.add(new Player(username, password));
                        }
                        Server.lockPlayersQueue.unlock();
                        break;

                    } else {
                        writer.println("registration failed!" + Registration.registerUser(username, password).split("-")[1]);
                    }

                } else {
                    System.out.println("Invalid option");
                }

            } catch (IOException ex) {
                System.out.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        System.out.println("saiii");
    }

}