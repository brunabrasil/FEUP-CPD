
import proj.Hangman;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        List<Map.Entry<Character, Boolean>> charList = List.of(
                Map.entry('A', true),
                Map.entry('P', true),
                Map.entry('P', true),
                Map.entry('L', false),
                Map.entry('E', false)
        );
        String hangmanAsciiArt = Hangman.getHangman(2, charList);
        System.out.println(hangmanAsciiArt);
    }
}