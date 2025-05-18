package fr.uge.splendor;
import java.util.*;

public class Game {
    private final List<Player> players = new ArrayList<>();
    private final TokenStack tokenStack = new TokenStack();
    private final List<CardStack> cardStack;
    private final NobleStack nobleStack = new NobleStack();
    private final ArrayList<Noble> noblesShow = new ArrayList<>();
    private final ArrayList<ArrayList<Card>> cardsShow = new ArrayList<>();
    private int tourNumber = 0;

    public Game(int numPlayers) {
        CardStack.loadCardFromCSV();
        cardStack = List.of(new CardStack(1),new CardStack(2),new CardStack(3));

        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player(i+1));
        }
        prepareBoard();

    }

    private int getNoblesToShow(){
        return players.size()+1;
    }

    private void prepareNobles() {
        int noblesToShow = getNoblesToShow();
        for (int i = 0; i < noblesToShow; i++) {
            Noble noble = (Noble) nobleStack.takeOne();
            noblesShow.add(noble);
        }
    }


    private void prepareBoard() {
        prepareNobles();
        prepareCards();
    }

    private void prepareCards() {
        for (CardStack stack : cardStack) {
            ArrayList<Card> cards = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                if (!stack.isEmpty()) {
                    Card card = (Card) stack.takeOne();
                    cards.add(card);
                }
            }
            cardsShow.add(cards);
        }
    }

    public void play() {
        // Boucle de jeu principale
        while (!haveWinner()){
            int playerIndex = tourNumber % players.size();

            Player currentPlayer = players.get(playerIndex);
            System.out.println("C'est le tour de " + currentPlayer.getName());



            tourNumber++;
        }
        announceWinner();
        /*while (!cardStack.isEmpty()) {
            for (Player player : players) {
                executeTurn(player);
                if (cardStack.isEmpty()) break;
            }
        }
        announceWinner();*/
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
            if (player.canBuyCard(card)) {
                player.buyCard(card);
            }
        }
    }

    private void showBoard() {
        System.out.println("Nobles : ");
        for (Noble noble : noblesShow) {
            System.out.println(noble);
        }
        System.out.println("Cartes : ");
        for (ArrayList<Card> cards : cardsShow) {
            for (Card card : cards) {
                System.out.println(card);
            }
        }
    }

    private boolean haveWinner(){
        return players.stream().anyMatch(player -> player.getPrestigePoints() >= 15);
    }

    private void announceWinner() {
        players.stream()
                .max(Comparator.comparingInt(Player::getPrestigePoints))
                .ifPresent(winner -> System.out.println("Le gagnant est " + winner.getName() + " avec " + winner.getPrestigePoints() + " points de prestige !"));
    }
}