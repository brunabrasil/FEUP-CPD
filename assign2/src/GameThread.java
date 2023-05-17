
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
    private int num = 5;

    public GameThread(List <Player> players, String gameId){
        this.gameId = gameId;
        this.gamePlayers = players;
    }

    public void run() {
        try {
            Selector selector = Selector.open();
            registerSocketChannel(selector);

            while (true){
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if(!key.isValid()){
                        continue;
                    }
                    if(key.isReadable()){
                        String response = read(key);
                        readGuess(key, response);
                    }

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readGuess(SelectionKey key, String guess) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();

        if (guess != null) {
            int clientNumber = Integer.parseInt(guess);
            String message;

            if (clientNumber == num) {
                message = "Correct guess!";
            } else if (clientNumber > num) {
                message = "Too high!";
            } else {
                message = "Too low!";
            }

            SocketChannelUtils.sendString(clientChannel, message);
        }
    }


    private String read(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();

        String messageReceived = SocketChannelUtils.receiveString(clientChannel);
        System.out.println(messageReceived);
        return messageReceived;
    }

    public void registerSocketChannel(Selector sel) throws IOException{
        for (Player player: gamePlayers){
            SocketChannel socketChannel = player.getChannel();

            socketChannel.configureBlocking(false);
            socketChannel.register(sel, SelectionKey.OP_READ);
            System.out.println("lala");

            SocketChannelUtils.sendString(socketChannel, "game started");
        }
    }

}