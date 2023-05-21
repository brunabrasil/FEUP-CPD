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
        long startTime = System.currentTimeMillis();

        while (true){
            try {
                Thread.sleep(1000); // Wait for some time before checking the queue again
                Server.lockPlayersQueue.lock();
                for (Player player : Server.playersQueue) {

                    System.out.println("|queue " + player.getUsername() + " " + player.getToken().getToken() + " "  + player.isLoggedIn() + " " + player.getRank() + "|");
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

                // Filter the list to get only logged-in players1
                List<Player> loggedInPlayers = Server.playersQueue.stream().filter(Player::isLoggedIn).collect(Collectors.toList());

                if(loggedInPlayers.size() >= 3){

                    List<Integer> playerRankDifference = calculateRankDifference(loggedInPlayers);

                    for(int i = 0; i < playerRankDifference.size(); i++){
                        // if i have a perfect match (rank difference = 0) -> create a game with those players
                        if(playerRankDifference.get(i) == 0){
                            System.out.println("perfect matchhh");
                            // create a new game thread
                            List<Player> gamePlayers = new ArrayList<>();
                            for (int j = 0; j < 3; j++) {
                                //remove da queue
                                Server.lockPlayersQueue.lock();
                                Server.playersQueue.remove(loggedInPlayers.get(i + j));
                                Server.lockPlayersQueue.unlock();

                                //adiciona a lista de jogadores para o novo jogo
                                gamePlayers.add(loggedInPlayers.get(i + j));
                            }

                            String gameId = UUID.randomUUID().toString();
                            Server.executor.submit(new GameThread(gamePlayers, gameId));

                            //recomeçar a contagem de tempo
                            startTime = System.currentTimeMillis();

                            break; // so consigo criar um jogo, porque posso tirar 3 jogadores e um deles fazer parte de outra combinação perfeita
                        }

                        // else, i want to create a game with the players with the smallest rank difference
                        else if (System.currentTimeMillis() - startTime >= 7000) {
                            System.out.println("not perfecttt");
                            // get the index of the player with the smallest rank difference
                            int minIndex = playerRankDifference.indexOf(Collections.min(playerRankDifference));
                            // create a new game thread
                            List<Player> gamePlayers = new ArrayList<>();
                            for (int j = 0; j < 3; j++) {
                                //remove da queue
                                Server.lockPlayersQueue.lock();
                                Server.playersQueue.remove(loggedInPlayers.get(i + j));
                                Server.lockPlayersQueue.unlock();

                                gamePlayers.add(loggedInPlayers.get(minIndex + j));
                            }

                            String gameId = UUID.randomUUID().toString();
                            Server.executor.submit(new GameThread(gamePlayers, gameId));

                            //recomeçar a contagem de tempo
                            startTime = System.currentTimeMillis();

                            break;
                        }
                        else {
                            System.out.println("ainda naoo");
                        }
                    }

                }

                /*if (Server.playersQueue.size() >= 2) {
                    // create a new game thread
                    List<Player> gamePlayers = new ArrayList<>();
                    for (int i = 0; i < 2; i++) {
                        gamePlayers.add(Server.playersQueue.poll());
                    }

                    String gameId = UUID.randomUUID().toString();
                    Server.executor.submit(new GameThread(gamePlayers, gameId));
                }*/

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