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

                    System.out.println(player.getUsername() + " " + player.getToken() + " "  + player.isLoggedIn());

                    if (player.getToken() != null && player.getToken().hasExpired() && player.isLoggedIn()) {

                        System.out.println("hereee");

                        if(isSocketChannelOpen(player.getChannel())){
                            System.out.println("hereee2");
                            TokenWithExpiration newToken = Authentication.generateToken(player.getUsername(), 1);
                            player.setToken(newToken);
                            System.out.println(newToken.getToken());
                            Authentication.writeTokenToFile(player.getUsername(), newToken.getToken());
                        }
                        else{
                            System.out.println("hereee3");
                            player.logout();
                            Server.playersQueue.remove(player); // desconectado e token expirado -> remove da queue

                        }
                    }

                }

                System.out.println("Queue size: " + Server.playersQueue.size());

                if (Server.playersQueue.size() >= 2) {
                    // create a new game thread
                    List<Player> gamePlayers = new ArrayList<>();
                    for (int i = 0; i < 2; i++) {
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
            System.out.println("Checking if socket channel is open");
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