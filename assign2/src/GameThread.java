
import java.io.*;
import java.net.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import proj.*;


public class GameThread extends Thread {

    //private static final int MAX_NUM_TRIES = 6; // Maximum number of tries for each player
    private List<Socket> sockets;
    String gameId;
    List<Player> gamePlayers;
    private BufferedReader reader;
    private PrintWriter writer;
    private ServerSocketChannel serverSocketChannel;
    private int num = generateRandomNumber();

    public volatile boolean endGame = false;

    public GameThread(List <Player> players, String gameId){
        this.gameId = gameId;
        this.gamePlayers = players;
    }

    public void run() {
        try {
            Selector selector = Selector.open();
            registerSocketChannel(selector);

            while (!endGame){
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if(!key.isValid()){
                        continue;
                    }
                    if(key.isReadable()){
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        String response = read(key);
                        response = readGuess(key, response);

                        if (response.startsWith("Correct")) {
                            endGame = true;
                        }
                        else {
                            SocketChannelUtils.sendString(clientChannel, response);

                        }
                    }
                }
            }
            for (Player player: gamePlayers){
                SocketChannel socketChannel = player.getChannel();

                SocketChannelUtils.sendString(socketChannel, "game ended - PLAYER TAL guessed the right number (" + num + ")");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readGuess(SelectionKey key, String guess) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();

        if (guess != null) {
            int clientNumber = Integer.parseInt(guess);
            String message;

            if (clientNumber == num) {
                message = "Correct guess!";
            } else if (clientNumber > num) {
                message = "Too high!";
            } else if (clientNumber < num) {
                message = "Too low!";
            } else {
                message = "Invalid";
            }
            return message;

        }

        return "";
    }


    private String read(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();

        String messageReceived = SocketChannelUtils.receiveString(clientChannel);

        return messageReceived;
    }

    public void registerSocketChannel(Selector sel) throws IOException{
        for (Player player: gamePlayers){
            SocketChannel socketChannel = player.getChannel();

            socketChannel.configureBlocking(false);
            socketChannel.register(sel, SelectionKey.OP_READ);

            SocketChannelUtils.sendString(socketChannel, "game started");
        }
    }

    // function to generate random number from 0 to 100
    public static int generateRandomNumber() {
        Random rand = new Random();
        return rand.nextInt(100);
    }

}

