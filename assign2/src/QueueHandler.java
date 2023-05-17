import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class QueueHandler implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000); // Wait for some time before checking the queue again

                Server.lockPlayersQueue.lock();
                if (Server.playersQueue.size() >= 1) {
                    // create a new game thread
                    List<Player> gamePlayers = new ArrayList<>();
                    for (int i = 0; i < 1; i++) {
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