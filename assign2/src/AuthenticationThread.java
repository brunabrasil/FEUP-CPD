
import java.io.*;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class AuthenticationThread extends Thread {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private SocketChannel socketChannel;

    public AuthenticationThread(Socket userSocket, SocketChannel socketChannel){
        this.socket = userSocket;
        this.socketChannel = socketChannel;
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
                String[] response = message.split(" ");
                if (response[0].equals("login")) {

                    String username = response[1];
                    String password = response[2];
                    String t = null;

                    if (Authentication.authenticatePlayer(username, password)) {

                        if(response.length >=4){
                            t = response[3]; // index out of bounds
                        }

                        Server.lockDB.lock();
                        Player player = Server.users.get(username);
                        Server.lockDB.unlock();

                        TokenWithExpiration token;
                        if(t != null){
                            token = player.getPlayerToken();
                            if(token != null){
                                if(!token.getToken().equals(t) || token.hasExpired()){

                                    //colocar a nulo o token
                                    player.setToken(null);

                                    writer.println("login failed problem with token");
                                    break;
                                }
                            }

                        }
                        else {
                            token = Authentication.generateToken(username, 1);
                            player.setToken(token);
                        }

                        if(Server.lockPlayersQueue.tryLock()){
                            Server.playersQueue.add(player);
                            Server.lockPlayersQueue.unlock();

                        }
                        player.setSocket(socket);
                        player.setChannel(socketChannel);
                        writer.println("login successfully " + token.getToken());

                        break;
                    } else {
                        writer.println("login failed");
                    }

                } else if (Objects.equals(message.split(" ")[0], "register")) {

                    String username = message.split(" ")[1];
                    String password = message.split(" ")[2];

                    Player player = Registration.registerUser(username, password);
                    if (player != null) {
                        TokenWithExpiration token = Authentication.generateToken(username, 1);
                        player.setToken(token);

                        //trylock tenta uma vez e passa, é non blocking // lock fica travado e tentando ate conseguir, é blocking //decidir isso
                        if(Server.lockPlayersQueue.tryLock()){
                            Server.playersQueue.add(player);
                            Server.lockPlayersQueue.unlock();

                        }
                        player.setSocket(socket);
                        player.setChannel(socketChannel);
                        writer.println("registration successfully " + token.getToken());

                        break;

                    } else {
                        writer.println("registration failed");
                    }
                }
                else {
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