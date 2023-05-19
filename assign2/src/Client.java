
import java.net.*;
import java.io.*;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Client {

    private static final int TIMEOUT = 1000; // Timeout in milliseconds

    public static void main(String[] args) throws IOException {
        if (args.length < 2) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(hostname, port));
        Socket socket = socketChannel.socket();

        String username = null;

        Scanner scan = new Scanner(System.in);
        System.out.println("\nWelcome to Guessing the number game! Select an option to start the game\n");

        while (true) {
            System.out.println("1 - LOGIN \n2 - REGISTER \nquit - LEAVE");
            String option = scan.nextLine();
            if (!option.equalsIgnoreCase("1") && !option.equalsIgnoreCase("2") && !option.equalsIgnoreCase("quit")) {
                System.out.println("Invalid option2");
                continue;
            }
            if(option.equals("quit")) {
                System.out.println("Bye bye");
                break;
            }

            System.out.println("USERNAME: ");
            username = scan.nextLine();
            System.out.println("PASSWORD: ");
            String password = scan.nextLine();


            switch (option) {
                case "1":

                    String token = null;
                    try (Scanner scanner = new Scanner(new File("token_" + username + ".txt"))) {
                        token = scanner.nextLine();
                    } catch (FileNotFoundException e) {
                        // The token file does not exist
                    }
                    String opt = "2"; //vai fazer nova conexao por default

                    //if there is a token in a file
                    if(token != null) {
                        while (true){
                            System.out.println("\nYou have lost a connection!\n1 - Resume the connection \n2 - Create a new connection\n");
                            opt = scan.nextLine();

                            OutputStream output = socket.getOutputStream();
                            PrintWriter writer = new PrintWriter(output, true);

                            //new connection, dont use token
                            if(opt.equals("2")){
                                writer.println("login " + username + " " + password);
                                break;
                            }
                            //resume connection with token in file
                            else if(opt.equals("1")){
                                writer.println("login " + username + " " + password + " " + token);
                                break;
                            }

                            else{
                                continue;
                            }
                        }
                    } else {
                        OutputStream output = socket.getOutputStream();
                        PrintWriter writer = new PrintWriter(output, true);
                        writer.println("login " + username + " " + password);
                    }

                    break;
                //register
                case "2":
                    OutputStream outputReg = socket.getOutputStream();
                    PrintWriter writerReg = new PrintWriter(outputReg, true);
                    writerReg.println("register " + username + " " + password);
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String response = reader.readLine();
            System.out.println(response);

            if (response.split(" ")[1].equals("successfully")){
                //if authentication success, write token file
                Authentication.writeTokenToFile(username, response.split(" ")[2]);
                break;
            }
            else if(response.split(" ")[1].equals("failed")){
                if(response.split(" ")[0].equals("login")){
                    //if login failed, delete file token
                    deleteFileToken(username);
                }

                continue;
            }
            else {
                continue;
            }

        }

        /*ServerListener serverListener = new ServerListener(socketChannel);
        Thread serverListenerThread = new Thread(serverListener);
        serverListenerThread.start();*/

        while (true){

            System.out.println("quero jogar de novo");
            //game
            String message = SocketChannelUtils.receiveString(socketChannel);
            System.out.println(message);


            if(message.equals("game started") || message.startsWith("Too")) {
                System.out.println("Choose a number to guess [1-100]");
                String guess = scan.nextLine();

                //String guess = readUserInputWithTimeout();
                //FALTA: ver se ta nesse intervalo

                if (guess.equals("quit")) {
                    deleteFileToken(username);
                    System.out.println("Bye bye");
                    break;
                }
                else {
                    SocketChannelUtils.sendString(socketChannel, guess);
                }
            }
            // Check if the game has ended
            else if (message.startsWith("game ended")) {

                System.out.println("\nDo you want to play again?\n1 - YES\n2 - NO");
                String playAgain = scan.nextLine();
                if(playAgain.equals("1")){
                    SocketChannelUtils.sendString(socketChannel, "yes");
                    continue;
                } else if(playAgain.equals("2")){
                    SocketChannelUtils.sendString(socketChannel, "no");

                    break;
                }

            }

        }
        System.out.println("kakakak");

    }

    private static String readUserInputWithTimeout() throws IOException {
        BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
        String userInput = null;

        try {
            // Wait for user input with a timeout
            long startTime = System.currentTimeMillis();
            while ((System.currentTimeMillis() - startTime) < TIMEOUT && !userInputReader.ready()) {
                TimeUnit.MILLISECONDS.sleep(100); // Adjust the sleep duration as needed
            }

            if (userInputReader.ready()) {
                userInput = userInputReader.readLine();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return userInput;
    }

    private static void deleteFileToken(String username){
        File tokenFile = new File("token_" + username + ".txt");
        if (tokenFile.exists()) {
            if (tokenFile.delete()) {
                System.out.println("Token file deleted successfully");
            } else {
                System.out.println("Failed to delete token file");
            }
        }
    }

}

