package fr.uge.splendor.tools;

import fr.uge.splendor.model.Card;
import fr.uge.splendor.model.GameColor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Tools {

    public static final  int INDEX_LEVEL = 0;
    public static final int INDEX_PV = 2;
    public static final int INDEX_WHITE = 5;
    public static final int INDEX_BLUE = 6;
    public static final int INDEX_GREEN = 7;
    public static final int INDEX_RED = 8;
    public static final int INDEX_BLACK = 9;

    private static int parseIntOrDefault(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private  static int parseCSVIntField(String[] parts, int index){
        if (index < 0 || index >= parts.length) {
            return 0;
        }
        return parseIntOrDefault(parts[index], 0);
    }
    private static Card readCardLine(String line, GameColor currentColor, int currentLevel) {

        String[] parts = line.split(",");

        int level = parseCSVIntField(parts, INDEX_LEVEL);
        GameColor gemColor = GameColor.getGameColorFromString(parts[1]);
        // magic numbers NOOO
        int pv = parseCSVIntField(parts,INDEX_PV);
        int white = parseCSVIntField(parts, INDEX_WHITE);
        int blue = parseCSVIntField(parts, INDEX_BLUE);
        int green = parseCSVIntField(parts, INDEX_GREEN);
        int red = parseCSVIntField(parts, INDEX_RED);
        int black = parseCSVIntField(parts, INDEX_BLACK);

        Map<GameColor, Integer> price = new TreeMap<>();
        price.put(GameColor.WHITE, white);
        price.put(GameColor.BLUE, blue);
        price.put(GameColor.GREEN, green);
        price.put(GameColor.RED, red);
        price.put(GameColor.BLACK, black);

        if (gemColor == null) {
            gemColor = currentColor;
        }
        if (level == 0) {
            level = currentLevel;
        }

        return new Card(level, gemColor, pv, price);
    }

    public static List<Card> LoadingCardsOnCSV(Path path) throws IOException {
        ArrayList<Card> cards = new ArrayList<>();

        int header_size = 2;
        try(var reader = Files.newBufferedReader(path)) {


            String line;
            GameColor currentColor = null;
            int currentLevel = 0;
            while ((line = reader.readLine()) != null) {
                if (header_size > 0) {
                    header_size--;
                    continue;
                }

                Card card = readCardLine(line,currentColor,currentLevel); // if we want to upgrade this we can use info on header
                currentColor = card.color();
                currentLevel = card.level();
                cards.add(card);
            }
        }
        return List.copyOf(cards); // Immutable list

    }
}
