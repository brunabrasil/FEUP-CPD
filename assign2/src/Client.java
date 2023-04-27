

import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 * This program demonstrates a simple TCP/IP socket client.
 *
 * @author www.codejava.net
 */
public class Client {

    public static void main(String[] args) {
        if (args.length < 2) return;

        Scanner scan = new Scanner(System.in);

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(hostname, port)) {

            while (true) {
                System.out.println("Welcome to proj.Hangman! Select an option to start the game\n");
                System.out.println("1 - LOGIN \n 2 - REGISTER \n");
                String option = scan.nextLine();
                if (!option.equalsIgnoreCase("1") && !option.equalsIgnoreCase("1")) {
                    System.out.println("Invalid option");
                    continue;
                }

                System.out.println("USERNAME: ");
                String username = scan.nextLine();
                System.out.println("PASSWORD: ");
                String password = scan.nextLine();

                switch (option) {
                    case "1":

                        OutputStream output = socket.getOutputStream();
                        PrintWriter writer = new PrintWriter(output, true);
                        writer.println("login " + username + " " + password);

                        break;

                    case "2":
                        OutputStream outputReg = socket.getOutputStream();
                        PrintWriter writerReg = new PrintWriter(outputReg, true);
                        writerReg.println("register " + username + " " + password);

                        break;

                    default:
                        System.out.println("Invalid option");
                        break;
                }

                /*
                if (option.equals("exit")) {
                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String numOfTries = reader.readLine();
                    System.out.println("Number of tries left: " + numOfTries);
                    break;
                }
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                writer.println(num);

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String sum = reader.readLine();
                //System.out.println("Soma: " + scan.close());*/
            }
        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}