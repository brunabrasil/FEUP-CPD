

import java.net.*;
import java.io.*;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        if (args.length < 2) return;


        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(hostname, port)) {
            Scanner scan = new Scanner(System.in);
            System.out.println("\nWelcome to Hangman! Select an option to start the game\n");

            while (true) {
                System.out.println("1 - LOGIN \n2 - REGISTER \nquit - LEAVE");
                String option = scan.nextLine();
                if (!option.equalsIgnoreCase("1") && !option.equalsIgnoreCase("2") && !option.equalsIgnoreCase("quit")) {
                    System.out.println("Invalid option");
                    continue;
                }
                if(option.equals("quit")) {
                    System.out.println("Bye bye");
                    break;
                }

                System.out.println("USERNAME: ");
                String username = scan.nextLine();
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
                        if(token != null){
                            System.out.println("\nYou have lost a connection!\n1 - Resume the connection \n2 - Create a new connection\n");
                            opt = scan.nextLine();
                        }

                        OutputStream output = socket.getOutputStream();
                        PrintWriter writer = new PrintWriter(output, true);
                        if(opt.equals("2")){
                            writer.println("login " + username + " " + password);
                        }
                        else if(opt.equals("1")){
                            writer.println("login " + username + " " + password + " " + token);
                        }

                        break;

                    case "2":
                        OutputStream outputReg = socket.getOutputStream();
                        PrintWriter writerReg = new PrintWriter(outputReg, true);
                        writerReg.println("register " + username + " " + password);
                    default:
                        System.out.println("Invalid option");
                        break;
                }

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String response = reader.readLine();
                System.out.println(response);

                if(response.split(" ")[0].equals("login")){
                    if(response.split(" ")[1].equals("failed")){

                        File tokenFile = new File("token_" + username + ".txt");
                        if (tokenFile.exists()) {
                            if (tokenFile.delete()) {
                                System.out.println("Token file deleted successfully");
                            } else {
                                System.out.println("Failed to delete token file");
                            }
                        }
                        continue;
                    }
                    else if (response.split(" ")[1].equals("successfully")){

                        try (FileWriter fileWriter = new FileWriter("token_" + username + ".txt")) {
                            fileWriter.write(response.split(" ")[2]);
                        } catch (IOException e) {
                            // Handle the exception appropriately
                        }
                        break;
                    }

                }
            }

            SocketChannel socketChannel = socket.getChannel();
            socketChannel.configureBlocking(false);

            while(true){
                System.out.println("Message::"+SocketChannelUtils.receiveString(socketChannel));
                //gameloop
            }
        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }

    }

}