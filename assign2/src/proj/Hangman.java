package proj;

import java.util.List;
import java.util.Map;

public class Hangman {
    public static final String[] HANGMAN_ASCII = {
            "  +---+",
            "  |   |",
            "  |   O",
            "  |  /|\\",
            "  |  / \\",
            " _|_",
            "|   |______",
            "|          |",
            "|__________|",
    };

    public static final String[] HANGMAN_ASCII_IITIAL = {
            "  +---+",
            "  |    ",
            "  |    ",
            "  |     ",
            "  |     ",
            " _|_",
            "|   |______",
            "|          |",
            "|__________|",
    };

    public static String getHangman(int numOfErrors, List<Map.Entry<Character, Boolean>> charList) {
        StringBuilder sb = new StringBuilder();

        for (String s : HANGMAN_ASCII_IITIAL) {
            sb.append(s);
            sb.append("\n");
        }

        switch (numOfErrors) {
            case 1 -> sb.replace(14, 15, "|");
            case 2 -> sb.replace(15, 16, "O");
            case 3 -> sb.replace(22, 23, "|");
            case 4 -> sb.replace(22, 23, "|\\");
            case 5 -> sb.replace(24, 25, "/");
            case 6 -> sb.replace(26, 27, "\\");
        }

        sb.append("\n");
        for (Map.Entry<Character, Boolean> entry : charList) {
            if (entry.getValue()) {
                sb.append(entry.getKey());
            } else {
                sb.append("_");
            }
            sb.append(" ");
        }

        return sb.toString();
    }
}