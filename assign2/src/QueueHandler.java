import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class QueueHandler implements Runnable {
    //Selector selector = Selector.open();

    @Override
    public void run() {
        while (true){

            try {
                Thread.sleep(1000); // Wait for some time before checking the queue again
                Server.lockPlayersQueue.lock();
                for (Player player : Server.playersQueue) {

                    System.out.println("|" + player.getUsername() + " " + player.getToken().getToken() + " "  + player.isLoggedIn() + " " + player.getRank() + "|");
                    if(isSocketChannelOpen(player.getChannel())) {
                        if (player.getToken() != null && player.getToken().hasExpired() && player.isLoggedIn()) {

                            TokenWithExpiration newToken = Authentication.generateToken(player.getUsername(), 1);
                            player.setToken(newToken);
                            Authentication.writeTokenToFile(player.getUsername(), newToken.getToken());
                        }
                    }
                    else {
                        player.logout();
                        if (player.getToken() != null && player.getToken().hasExpired()) {
                            Server.playersQueue.remove(player); // desconectado e token expirado -> remove da queue

                        }
                    }

                }

                System.out.println("Queue size: " + Server.playersQueue.size());

                if (Server.playersQueue.size() >= 3) {
                    // create a new game thread
                    List<Player> gamePlayers = new ArrayList<>();
                    for (int i = 0; i < 3; i++) {
                        gamePlayers.add(Server.playersQueue.poll());
                    }

                    String gameId = UUID.randomUUID().toString();
                    Server.executor.submit(new GameThread(gamePlayers, gameId));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Server.lockPlayersQueue.unlock();
            }

        }
    }


    private boolean isSocketChannelOpen(SocketChannel socketChannel) {
        try {
            // Attempt a non-blocking read operation

            socketChannel.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(1);
            int bytesRead = socketChannel.read(buffer);

            return bytesRead != -1; // If bytesRead is -1, the client has disconnected
        } catch (IOException e) {
            System.out.println("Error while checking if socket channel is open");
            return false;
        }
    }
}