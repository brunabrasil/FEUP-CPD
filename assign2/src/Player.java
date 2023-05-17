import java.net.Socket;
import java.nio.channels.SocketChannel;

public class Player {
    private String username;
    private String password;
    private boolean isLoggedIn;
    private TokenWithExpiration token;
    private SocketChannel channel;
    private Socket socket;

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.isLoggedIn = true;
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

    public TokenWithExpiration getPlayerToken() {
        return token;
    }

    public void logout() {
        this.isLoggedIn = false;
    }

}