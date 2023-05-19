import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

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
                    if(player.isSocketChannelOpen()) {
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

                // Filter the list to get only logged-in players

                List<Player> loggedInPlayers = Server.playersQueue.stream().filter(Player::isLoggedIn).collect(Collectors.toList());

                calculateRankDifference(loggedInPlayers);

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


    public static List<Integer> calculateRankDifference(List<Player> loggedInPlayers) {
        List<Integer> rankDifferences = new ArrayList<>();

        // Iterate over the logged-in players in groups of three
        for (int i = 0; i < loggedInPlayers.size(); i += 3) {
            if (i + 2 < loggedInPlayers.size()) {
                Player player1 = loggedInPlayers.get(i);
                //Player player2 = loggedInPlayers.get(i + 1);
                Player player3 = loggedInPlayers.get(i + 2);

                // Calculate the difference in rank between the first and third player
                int rankDifference = Math.abs(getRankIndex(player1.getRank()) - getRankIndex(player3.getRank()));
                rankDifferences.add(rankDifference);
            }
        }

        return rankDifferences;
    }

    // Helper method to assign a numerical index to the rank
    private static int getRankIndex(String rank) {
        switch (rank) {
            case "A":
                return 0;
            case "B":
                return 1;
            case "C":
                return 2;
            default:
                return 3;
        }
    }

}