package fr.uge.splendor;
import java.util.*;

public class Game {
    private final List<Player> players = new ArrayList<>();
    private final Map<GameColor,TokenStack> tokenStack = new HashMap<>();
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
        prepareTokens();
        prepareNobles();
        prepareCards();
    }

    private void prepareTokens() {
        Arrays.stream(GameColor.values()).forEach(color -> tokenStack.put(color, new TokenStack(color, players.size())));
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
            showState(currentPlayer);

            tourNumber++;
            break;

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
            Token token = (Token) tokenStack.get(0).takeOne();
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

    private void showState(Player player) {
        System.out.println("C'est le tour de " + player.getName());
        showBoard();
        System.out.println();
        System.out.println("État du joueur :");
        player.showState();
    }

    private void showBoard() {
        showNobles();
        showCards();
        showTokens();
        showScore();
    }

    private void showScore() {
        System.out.print("Scores : ");
        StringJoiner joiner = new StringJoiner("\t");
        for (Player player : players) {
            joiner.add(player.getName() + ": " + player.getPrestigePoints() + "PT");
        }
        System.out.println(joiner);
    }

    private void showCards() {
        cardsShow.forEach(cards -> {
            System.out.print("Niveau " + (cards.get(0).level())+ " : ");
            StringJoiner joiner = new StringJoiner("\t");
            for (Card card : cards) {
                joiner.add(card.toString());
            }
            System.out.println(joiner);
        });
    }

    private void showNobles() {
        System.out.print("Nobles : ");
        StringJoiner joiner = new StringJoiner("\t");
        for (Noble noble : noblesShow) {
            joiner.add(noble.toString());
        }
        System.out.println(joiner);
    }

    private void showTokens() {
        System.out.print("Jetons : ");
        StringJoiner joiner = new StringJoiner("\t");
        for (TokenStack tokenStack : tokenStack.values()) {
            joiner.add(tokenStack.toString());
        }
        System.out.println(joiner);
    }

    private boolean haveWinner(){
        return players.stream().anyMatch(player -> player.getPrestigePoints() >= 15);
    }

    private void announceWinner() {
        players.stream()
                .max(Comparator.comparingInt(Player::getPrestigePoints))
                .ifPresent(winner -> System.out.println("Le gagnant est " + winner.getName() + " avec " + winner.getPrestigePoints() + " points de prestige !"));
    }

    private void refillCardsShowed(){
        for (int i = 0; i < cardsShow.size(); i++) { // we need i for know which level we need
            ArrayList<Card> cards = cardsShow.get(i);
            for (int j = 0; j < cards.size(); j++) {
                if (cards.get(j) == null) {
                    Card card = (Card) cardStack.get(i).takeOne();
                    cards.set(j, card);
                }
            }
        }
    }

    private void refillTokenStack(Map<GameColor, Integer> tokens){
        for (Map.Entry<GameColor, Integer> entry : tokens.entrySet()) {
            GameColor color = entry.getKey();
            int quantity = entry.getValue();
            tokenStack.computeIfPresent(color, (k, v) -> {
                v.refill(quantity);
                return v;
            });
        }

    }


    private void playerTakeCard(Player player, Card card){

    }
}