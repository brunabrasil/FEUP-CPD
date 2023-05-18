import java.nio.channels.Selector;
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

                    if (player.getToken() != null && player.getToken().hasExpired()) {
                        System.out.println(player.getSocket().isClosed());
                        System.out.println(player.getSocket().isConnected());

                        if(player.getChannel().isConnected()){
                            TokenWithExpiration newToken = Authentication.generateToken(player.getUsername(), 1);
                            player.setToken(newToken);
                            System.out.println(newToken.getToken());
                            Authentication.writeTokenToFile(player.getUsername(), newToken.getToken());
                        }
                        else{
                            player.logout();
                            System.out.println("merda");
                        }
                    }

                }

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
}