
public class Player {
    private String username;
    private String password;
    private boolean isLoggedIn;
    private TokenWithExpiration token;

    public Player(String username, String password, TokenWithExpiration token) {
        this.username = username;
        this.password = password;
        this.isLoggedIn = true;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setToken(TokenWithExpiration token) {
        this.token = token;
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