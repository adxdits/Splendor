package fr.uge.splendor;
import java.util.*;

public class Game {
    private final List<Player> players = new ArrayList<>();
    private final TokenStack tokenStack = new TokenStack();
    private final List<CardStack> cardStack;
    private final NobleStack nobleStack = new NobleStack();

    public Game(int numPlayers) {
        CardStack.loadCardFromCSV();
        cardStack = List.of(new CardStack(1),new CardStack(2),new CardStack(3));

        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player());
        }

    }

    public void play() {
        while (!cardStack.isEmpty()) {
            for (Player player : players) {
                executeTurn(player);
                if (cardStack.isEmpty()) break;
            }
        }
        announceWinner();
    }

    private void executeTurn(Player player) {
        // Prendre 2 jetons de même couleur
        if (!tokenStack.isEmpty()) {
            Token token = (Token) tokenStack.takeOne();
            player.addTokens(token.color(), 2);
        }

        // Acheter une carte si possible
        if (!cardStack.isEmpty()) {
            Card card = (Card) cardStack.get(0).takeOne();
            if (player.canBuy(card)) {
                player.addCard(card);
            }
        }
    }

    private void announceWinner() {
        players.stream()
                .max(Comparator.comparingInt(Player::getPrestigePoints))
                .ifPresent(winner -> System.out.println("Vainqueur: " + winner.getPrestigePoints() + " points"));
    }
}