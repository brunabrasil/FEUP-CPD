import java.net.Socket;
import java.nio.channels.SocketChannel;

public class Player {
    private String username;
    private String password;
    private boolean isLoggedIn;
    private TokenWithExpiration token;
    private SocketChannel channel;
    private Socket socket;
    private int points;

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.isLoggedIn = true;
        this.points = 0;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Socket getSocket() {
        return socket;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void setToken(TokenWithExpiration token) {
        this.token = token;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setChannel(SocketChannel socketChannel) {
        this.channel = socketChannel;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public TokenWithExpiration getToken() {
        return token;
    }

    public void logout() {
        this.isLoggedIn = false;
    }

    public String getRank(){
        if (points >= 50) {
            return "A";
        } else if (points >= 30) {
            return "B";
        } else if (points >= 20) {
            return "C";
        } else {
            return  "D";
        }
    }

    public void setWinningPoints(){
        this.points += 10;
    }

    public void setLosingPoints(){
        if(this.points < 0){
            this.points = 0;
        }
        else {
            this.points -= 0;
        }

    }
}