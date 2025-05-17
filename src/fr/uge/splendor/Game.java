package fr.uge.splendor;
import java.util.*;

public class Game {
    private final List<Player> players = new ArrayList<>();
    private final TokenStack tokenStack;
    private final CardStack cardStack;

    public Game() {
        this.tokenStack = new TokenStack();
        this.cardStack = new CardStack();
        players.add(new Player());
        players.add(new Player());
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
            Card card = (Card) cardStack.takeOne();
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