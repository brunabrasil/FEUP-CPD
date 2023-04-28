
public class Player {
    private String username;
    private String password;
    private boolean isLoggedIn;

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

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void logout() {
        this.isLoggedIn = false;
    }



}