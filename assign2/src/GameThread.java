
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
    private  ServerSocketChannel serverSocketChannel;
    public GameThread(List <Player> players, String gameId){
        //this.sockets = userSockets;
        /*try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
                        read(key);
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();

        String messageReceived = SocketChannelUtils.receiveString(clientChannel);
        System.out.println(messageReceived);
    }

    private void accept(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel playerChannel = serverSocketChannel.accept();
        playerChannel.configureBlocking(false);

        playerChannel.register(selector,SelectionKey.OP_READ);
        SocketChannelUtils.sendString(playerChannel,"JOGO COMEÇOU");
    }
    public void registerSocketChannel(Selector sel) throws IOException{
        for (Player player: gamePlayers){
            SocketChannel socketChannel = player.getChannel();
            socketChannel.register(sel, SelectionKey.OP_READ);
            SocketChannelUtils.sendString(socketChannel, "JOGO COMEÇOUU");
        }
    }

    /*private String chooseWord() {
        // Choose a word randomly from a list
        List<String> word = Arrays.asList("Apple", "Bicycle", "Elephant", "Garden", "Monkey", "Pizza", "Rainbow", "Soccer", "Jazz", "Hamburger");
        Random random = new Random();
        int index = random.nextInt(word.size());
        return word.get(index);
    }

    private List<Map.Entry<Character, Boolean>> getCharList(String wordChosen) {
        // Generate a list of character entries for the chosen word
        List<Map.Entry<Character, Boolean>> pairs = new ArrayList<>();
        for (char c : wordChosen.toCharArray()) {
            pairs.add(new AbstractMap.SimpleEntry<>(c, false));
        }
        return pairs;
    }*/


    /*public void updateNumOfTries(){
        lock.lock();
        int numOfTries = 1;
        lock.unlock();
    }*/
}